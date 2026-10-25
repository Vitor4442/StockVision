package stockvision.finance.Settlement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import stockvision.finance.Settlement.FinancialSettlement;
import stockvision.finance.Settlement.FinancialSettlementMethod;
import stockvision.finance.Settlement.FinancialSettlementStatus;

public record FinancialSettlementResponse(
        UUID id,
        UUID financialEntryId,
        BigDecimal amount,
        LocalDateTime paidAt,
        UUID financialAccountId,
        FinancialSettlementMethod method,
        FinancialSettlementStatus status
) {
    public static FinancialSettlementResponse from(FinancialSettlement settlement) {
        return new FinancialSettlementResponse(settlement.getId(), settlement.getFinancialEntryId(),
                settlement.getAmount(), settlement.getPaidAt(), settlement.getFinancialAccountId(),
                settlement.getMethod(), settlement.getStatus());
    }
}
