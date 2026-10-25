package stockvision.finance.Financial.dto;

import java.math.BigDecimal;

public record FinancialCurrencyBalanceResponse(
        String currency,
        BigDecimal initialBalance,
        BigDecimal confirmedReceivables,
        BigDecimal confirmedPayables,
        BigDecimal currentBalance
) {}
