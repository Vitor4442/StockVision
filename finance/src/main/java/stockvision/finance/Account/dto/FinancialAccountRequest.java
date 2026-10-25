package stockvision.finance.Account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import stockvision.finance.Account.FinancialAccountType;

public record FinancialAccountRequest(
        @NotBlank String name,
        @NotNull FinancialAccountType type,
        String institution,
        String accountNumber,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency,
        @NotNull BigDecimal initialBalance
) {}
