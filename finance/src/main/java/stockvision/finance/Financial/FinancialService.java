package stockvision.finance.Financial;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import stockvision.finance.Account.FinancialAccount;
import stockvision.finance.Account.FinancialAccountRepository;
import stockvision.finance.Category.FinancialCategory;
import stockvision.finance.Category.FinancialCategoryRepository;
import stockvision.finance.Category.FinancialCategoryStatus;
import stockvision.finance.Category.exception.FinancialCategoryConflictException;
import stockvision.finance.Category.exception.FinancialCategoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stockvision.finance.Financial.dto.FinancialEntryRequest;
import stockvision.finance.Financial.dto.FinancialEntryResponse;
import stockvision.finance.Financial.dto.CompanyBalanceResponse;
import stockvision.finance.Financial.dto.FinancialAccountBalanceResponse;
import stockvision.finance.Financial.dto.FinancialCurrencyBalanceResponse;
import stockvision.finance.Financial.exception.FinancialEntryNotFoundException;
import stockvision.finance.Financial.exception.FinancialEntryStateException;
import stockvision.finance.Settlement.FinancialSettlementRepository;
import stockvision.finance.Settlement.FinancialSettlementStatus;
import stockvision.finance.Settlement.dto.FinancialSettlementAccountTotal;

@Service
public class FinancialService {
    private final FinanciaRepository repository;
    private final FinancialSettlementRepository settlementRepository;
    private final FinancialCategoryRepository categoryRepository;
    private final FinancialAccountRepository accountRepository;

    public FinancialService(FinanciaRepository repository, FinancialSettlementRepository settlementRepository,
            FinancialCategoryRepository categoryRepository, FinancialAccountRepository accountRepository) {
        this.repository = repository;
        this.settlementRepository = settlementRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public CompanyBalanceResponse getCompanyBalance() {
        Map<UUID, BigDecimal[]> settlementsByAccount = new HashMap<>();
        for (FinancialSettlementAccountTotal total : settlementRepository.summarizeAmountsByAccountAndEntryType(FinancialSettlementStatus.CONFIRMED)) {
            BigDecimal[] amounts = settlementsByAccount.computeIfAbsent(total.financialAccountId(),
                    ignored -> new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO});
            int index = total.type() == FinancialType.RECEIVABLE ? 0 : 1;
            amounts[index] = amounts[index].add(total.amount());
        }

        Map<String, BigDecimal[]> totalsByCurrency = new LinkedHashMap<>();
        List<FinancialAccountBalanceResponse> accountBalances = accountRepository.findAll().stream()
                .map(account -> accountBalance(account, settlementsByAccount, totalsByCurrency)).toList();

        List<FinancialCurrencyBalanceResponse> currencyBalances = totalsByCurrency.entrySet().stream()
                .map(entry -> {
                    BigDecimal[] totals = entry.getValue();
                    return new FinancialCurrencyBalanceResponse(entry.getKey(), totals[0], totals[1], totals[2],
                            totals[0].add(totals[1]).subtract(totals[2]));
                }).toList();
        return new CompanyBalanceResponse(currencyBalances, accountBalances);
    }

    private FinancialAccountBalanceResponse accountBalance(FinancialAccount account,
            Map<UUID, BigDecimal[]> settlementsByAccount, Map<String, BigDecimal[]> totalsByCurrency) {
        BigDecimal[] settledAmounts = settlementsByAccount.getOrDefault(account.getId(),
                new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO});
        BigDecimal currentBalance = account.getInitialBalance().add(settledAmounts[0]).subtract(settledAmounts[1]);

        BigDecimal[] currencyTotals = totalsByCurrency.computeIfAbsent(account.getCurrency(),
                ignored -> new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});
        currencyTotals[0] = currencyTotals[0].add(account.getInitialBalance());
        currencyTotals[1] = currencyTotals[1].add(settledAmounts[0]);
        currencyTotals[2] = currencyTotals[2].add(settledAmounts[1]);

        return new FinancialAccountBalanceResponse(account.getId(), account.getName(), account.getType(),
                account.getStatus(), account.getDeletedAt(), account.getCurrency(), account.getInitialBalance(),
                settledAmounts[0], settledAmounts[1], currentBalance);
    }

    @Transactional
    public FinancialEntryResponse create(FinancialType type, FinancialEntryRequest request) {
        if (request.categoryId() != null) {
            FinancialCategory category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new FinancialCategoryNotFoundException(request.categoryId()));
            if (category.getDeletedAt() != null || category.getStatus() != FinancialCategoryStatus.ACTIVE) {
                throw new FinancialCategoryConflictException("Only active financial categories can be assigned.");
            }
            if (category.getType() != type) {
                throw new FinancialCategoryConflictException("Category type must match the financial entry type.");
            }
        }
        FinancialEntity entity = new FinancialEntity();
        entity.setType(type);
        entity.setDescription(request.description().trim());
        entity.setAmount(request.amount());
        entity.setIssueDate(request.issueDate());
        entity.setDueDate(request.dueDate());
        entity.setCategoryId(request.categoryId());
        entity.setCounterpartyId(request.counterpartyId());
        entity.setSourceType(request.sourceType());
        entity.setSourceId(request.sourceId());
        entity.setStatus(FinancialStatus.PENDING);
        return FinancialEntryResponse.from(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<FinancialEntryResponse> findAll(FinancialType type, FinancialStatus status,
            LocalDate issueDateFrom, LocalDate issueDateTo, LocalDate dueDateFrom, LocalDate dueDateTo,
            UUID categoryId, UUID counterpartyId, boolean includeDeleted, Pageable pageable) {
        Specification<FinancialEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!includeDeleted) predicates.add(cb.isNull(root.get("deletedAt")));
            if (type != null) predicates.add(cb.equal(root.get("type"), type));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (issueDateFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("issueDate"), issueDateFrom));
            if (issueDateTo != null) predicates.add(cb.lessThanOrEqualTo(root.get("issueDate"), issueDateTo));
            if (dueDateFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
            if (dueDateTo != null) predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
            if (categoryId != null) predicates.add(cb.equal(root.get("categoryId"), categoryId));
            if (counterpartyId != null) predicates.add(cb.equal(root.get("counterpartyId"), counterpartyId));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(spec, pageable).map(FinancialEntryResponse::from);
    }

    @Transactional(readOnly = true)
    public FinancialEntryResponse findById(UUID id) {
        return FinancialEntryResponse.from(getEntry(id));
    }

    @Transactional
    public FinancialEntryResponse settle(UUID id) {
        FinancialEntity entity = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new FinancialEntryNotFoundException(id));
        ensureActive(entity);
        if (entity.getStatus() != FinancialStatus.PENDING) {
            throw new FinancialEntryStateException("Only pending financial entries can be settled.");
        }
        entity.setStatus(FinancialStatus.PAID);
        return FinancialEntryResponse.from(repository.save(entity));
    }

    @Transactional
    public FinancialEntryResponse cancel(UUID id) {
        FinancialEntity entity = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new FinancialEntryNotFoundException(id));
        ensureActive(entity);
        if (entity.getStatus() != FinancialStatus.PENDING) {
            throw new FinancialEntryStateException("Only pending financial entries can be cancelled.");
        }
        if (settlementRepository.sumAmountByFinancialEntryIdAndStatus(
                entity.getId(), FinancialSettlementStatus.CONFIRMED).signum() > 0) {
            throw new FinancialEntryStateException("Financial entries with confirmed settlements cannot be cancelled.");
        }
        entity.setStatus(FinancialStatus.CANCELLED);
        return FinancialEntryResponse.from(repository.save(entity));
    }

    @Transactional
    public FinancialEntryResponse softDelete(UUID id) {
        FinancialEntity entity = getEntry(id);
        ensureActive(entity);
        entity.setDeletedAt(LocalDateTime.now());
        return FinancialEntryResponse.from(repository.save(entity));
    }

    @Transactional
    public FinancialEntryResponse restore(UUID id) {
        FinancialEntity entity = getEntry(id);
        if (entity.getDeletedAt() == null) {
            throw new FinancialEntryStateException("Financial entry is not deleted.");
        }
        entity.setDeletedAt(null);
        return FinancialEntryResponse.from(repository.save(entity));
    }

    private FinancialEntity getEntry(UUID id) {
        return repository.findById(id).orElseThrow(() -> new FinancialEntryNotFoundException(id));
    }

    private void ensureActive(FinancialEntity entity) {
        if (entity.getDeletedAt() != null) {
            throw new FinancialEntryStateException("Deleted financial entries cannot be changed.");
        }
    }
}
