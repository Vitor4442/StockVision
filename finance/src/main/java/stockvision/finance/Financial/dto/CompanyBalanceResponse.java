package stockvision.finance.Financial.dto;

import java.util.List;

public record CompanyBalanceResponse(
        List<FinancialCurrencyBalanceResponse> balancesByCurrency,
        List<FinancialAccountBalanceResponse> accounts
) {}
