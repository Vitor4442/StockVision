package stockvision.finance.Settlement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stockvision.finance.Account.FinancialAccount;
import stockvision.finance.Account.FinancialAccountRepository;
import stockvision.finance.Account.FinancialAccountStatus;
import stockvision.finance.Account.exception.FinancialAccountNotFoundException;
import stockvision.finance.Financial.FinancialEntity;
import stockvision.finance.Financial.FinancialStatus;
import stockvision.finance.Financial.FinanciaRepository;
import stockvision.finance.Financial.exception.FinancialEntryNotFoundException;
import stockvision.finance.Financial.exception.FinancialEntryStateException;
import stockvision.finance.Settlement.dto.FinancialSettlementRequest;
import stockvision.finance.Settlement.dto.FinancialSettlementResponse;
import stockvision.finance.Settlement.exception.FinancialSettlementNotFoundException;

@Service
public class FinancialSettlementService {
    private final FinancialSettlementRepository settlementRepository;
    private final FinanciaRepository entryRepository;
    private final FinancialAccountRepository accountRepository;

    public FinancialSettlementService(FinancialSettlementRepository settlementRepository,
            FinanciaRepository entryRepository, FinancialAccountRepository accountRepository) {
        this.settlementRepository = settlementRepository;
        this.entryRepository = entryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public FinancialSettlementResponse create(FinancialSettlementRequest request) {
        FinancialEntity entry = entryRepository.findByIdForUpdate(request.financialEntryId())
                .orElseThrow(() -> new FinancialEntryNotFoundException(request.financialEntryId()));
        ensureEntryCanBeSettled(entry);
        FinancialAccount account = accountRepository.findById(request.financialAccountId())
                .orElseThrow(() -> new FinancialAccountNotFoundException(request.financialAccountId()));
        if (account.getDeletedAt() != null || account.getStatus() != FinancialAccountStatus.ACTIVE) {
            throw new FinancialEntryStateException("Only active financial accounts can be used for settlements.");
        }

        BigDecimal settled = settlementRepository.sumAmountByFinancialEntryIdAndStatus(
                entry.getId(), FinancialSettlementStatus.CONFIRMED);
        BigDecimal remaining = entry.getAmount().subtract(settled);
        if (request.amount().compareTo(remaining) > 0) {
            throw new FinancialEntryStateException("Settlement amount exceeds the remaining entry balance of "
                    + remaining + ".");
        }

        FinancialSettlement settlement = new FinancialSettlement();
        settlement.setFinancialEntryId(entry.getId());
        settlement.setFinancialAccountId(account.getId());
        settlement.setAmount(request.amount());
        settlement.setPaidAt(request.paidAt() == null ? LocalDateTime.now() : request.paidAt());
        settlement.setMethod(request.method());
        settlement.setStatus(FinancialSettlementStatus.CONFIRMED);
        FinancialSettlement saved = settlementRepository.save(settlement);

        if (settled.add(request.amount()).compareTo(entry.getAmount()) >= 0) {
            entry.setStatus(FinancialStatus.PAID);
            entryRepository.save(entry);
        }
        return FinancialSettlementResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<FinancialSettlementResponse> findAll(UUID financialEntryId) {
        List<FinancialSettlement> settlements = financialEntryId == null
                ? settlementRepository.findAll()
                : settlementRepository.findAllByFinancialEntryIdOrderByPaidAtDesc(financialEntryId);
        return settlements.stream().map(FinancialSettlementResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FinancialSettlementResponse findById(UUID id) {
        return FinancialSettlementResponse.from(settlementRepository.findById(id)
                .orElseThrow(() -> new FinancialSettlementNotFoundException(id)));
    }

    @Transactional
    public FinancialSettlementResponse cancel(UUID id) {
        FinancialSettlement settlement = settlementRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new FinancialSettlementNotFoundException(id));
        if (settlement.getStatus() != FinancialSettlementStatus.CONFIRMED) {
            throw new FinancialEntryStateException("Only confirmed settlements can be cancelled.");
        }
        FinancialEntity entry = entryRepository.findByIdForUpdate(settlement.getFinancialEntryId())
                .orElseThrow(() -> new FinancialEntryNotFoundException(settlement.getFinancialEntryId()));
        BigDecimal settledAfterCancel = settlementRepository.sumAmountByFinancialEntryIdAndStatus(
                entry.getId(), FinancialSettlementStatus.CONFIRMED).subtract(settlement.getAmount());
        settlement.setStatus(FinancialSettlementStatus.CANCELLED);
        entry.setStatus(settledAfterCancel.compareTo(entry.getAmount()) >= 0
                ? FinancialStatus.PAID : FinancialStatus.PENDING);
        entryRepository.save(entry);
        return FinancialSettlementResponse.from(settlementRepository.save(settlement));
    }

    private void ensureEntryCanBeSettled(FinancialEntity entry) {
        if (entry.getDeletedAt() != null) {
            throw new FinancialEntryStateException("Deleted financial entries cannot be settled.");
        }
        if (entry.getStatus() != FinancialStatus.PENDING) {
            throw new FinancialEntryStateException("Only pending financial entries can be settled.");
        }
    }
}
