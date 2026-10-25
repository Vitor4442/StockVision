package stockvision.finance.Account;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stockvision.finance.Account.dto.FinancialAccountRequest;
import stockvision.finance.Account.dto.FinancialAccountResponse;
import stockvision.finance.Account.exception.FinancialAccountNotFoundException;
import stockvision.finance.Financial.exception.FinancialEntryStateException;

@Service
public class FinancialAccountService {
    private final FinancialAccountRepository repository;

    public FinancialAccountService(FinancialAccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FinancialAccountResponse create(FinancialAccountRequest request) {
        FinancialAccount account = new FinancialAccount();
        apply(account, request);
        account.setStatus(FinancialAccountStatus.ACTIVE);
        return FinancialAccountResponse.from(repository.save(account));
    }

    @Transactional(readOnly = true)
    public Page<FinancialAccountResponse> findAll(FinancialAccountType type, FinancialAccountStatus status,
            boolean includeDeleted, Pageable pageable) {
        Specification<FinancialAccount> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!includeDeleted) predicates.add(cb.isNull(root.get("deletedAt")));
            if (type != null) predicates.add(cb.equal(root.get("type"), type));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(spec, pageable).map(FinancialAccountResponse::from);
    }

    @Transactional(readOnly = true)
    public FinancialAccountResponse findById(UUID id) {
        return FinancialAccountResponse.from(getAccount(id));
    }

    @Transactional
    public FinancialAccountResponse update(UUID id, FinancialAccountRequest request) {
        FinancialAccount account = getAccount(id);
        ensureActive(account);
        apply(account, request);
        return FinancialAccountResponse.from(repository.save(account));
    }

    @Transactional
    public FinancialAccountResponse changeStatus(UUID id, FinancialAccountStatus status) {
        FinancialAccount account = getAccount(id);
        ensureActive(account);
        account.setStatus(status);
        return FinancialAccountResponse.from(repository.save(account));
    }

    @Transactional
    public FinancialAccountResponse softDelete(UUID id) {
        FinancialAccount account = getAccount(id);
        ensureActive(account);
        account.setDeletedAt(LocalDateTime.now());
        return FinancialAccountResponse.from(repository.save(account));
    }

    @Transactional
    public FinancialAccountResponse restore(UUID id) {
        FinancialAccount account = getAccount(id);
        if (account.getDeletedAt() == null) {
            throw new FinancialEntryStateException("Financial account is not deleted.");
        }
        account.setDeletedAt(null);
        return FinancialAccountResponse.from(repository.save(account));
    }

    private void apply(FinancialAccount account, FinancialAccountRequest request) {
        account.setName(request.name().trim());
        account.setType(request.type());
        account.setInstitution(clean(request.institution()));
        account.setAccountNumber(clean(request.accountNumber()));
        account.setCurrency(request.currency().trim().toUpperCase(Locale.ROOT));
        account.setInitialBalance(request.initialBalance());
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private FinancialAccount getAccount(UUID id) {
        return repository.findById(id).orElseThrow(() -> new FinancialAccountNotFoundException(id));
    }

    private void ensureActive(FinancialAccount account) {
        if (account.getDeletedAt() != null) {
            throw new FinancialEntryStateException("Deleted financial accounts cannot be changed.");
        }
    }
}
