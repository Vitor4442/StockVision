package stockvision.finance.Financial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import stockvision.finance.Account.FinancialAccountStatus;
import stockvision.finance.Account.FinancialAccountType;

public record FinancialAccountBalanceResponse(
        UUID financialAccountId,
        String name,
        FinancialAccountType type,
        FinancialAccountStatus status,
        LocalDateTime deletedAt,
        String currency,
        BigDecimal initialBalance,
        BigDecimal confirmedReceivables,
        BigDecimal confirmedPayables,
        BigDecimal currentBalance
) {}
