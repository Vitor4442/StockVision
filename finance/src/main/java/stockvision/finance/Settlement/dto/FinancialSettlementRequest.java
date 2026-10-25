package stockvision.finance.Settlement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import stockvision.finance.Settlement.FinancialSettlementMethod;

public record FinancialSettlementRequest(
        @NotNull UUID financialEntryId,
        @NotNull @Positive BigDecimal amount,
        LocalDateTime paidAt,
        @NotNull UUID financialAccountId,
        @NotNull FinancialSettlementMethod method
) {}
