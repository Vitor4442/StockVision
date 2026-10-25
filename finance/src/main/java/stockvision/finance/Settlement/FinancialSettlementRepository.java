package stockvision.finance.Settlement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stockvision.finance.Settlement.dto.FinancialSettlementAccountTotal;

public interface FinancialSettlementRepository extends JpaRepository<FinancialSettlement, UUID> {
    List<FinancialSettlement> findAllByFinancialEntryIdOrderByPaidAtDesc(UUID financialEntryId);

    @Query("select coalesce(sum(s.amount), 0) from FinancialSettlement s "
            + "where s.financialEntryId = :entryId and s.status = :status")
    BigDecimal sumAmountByFinancialEntryIdAndStatus(@Param("entryId") UUID entryId,
            @Param("status") FinancialSettlementStatus status);

    @Query("select new stockvision.finance.Settlement.dto.FinancialSettlementAccountTotal("
            + "s.financialAccountId, e.type, sum(s.amount)) "
            + "from FinancialSettlement s, FinancialEntity e "
            + "where s.financialEntryId = e.id and s.status = :status "
            + "group by s.financialAccountId, e.type")
    List<FinancialSettlementAccountTotal> summarizeAmountsByAccountAndEntryType(
            @Param("status") FinancialSettlementStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from FinancialSettlement s where s.id = :id")
    Optional<FinancialSettlement> findByIdForUpdate(@Param("id") UUID id);
}
