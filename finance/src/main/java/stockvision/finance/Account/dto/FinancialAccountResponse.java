package stockvision.finance.Account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import stockvision.finance.Account.FinancialAccount;
import stockvision.finance.Account.FinancialAccountStatus;
import stockvision.finance.Account.FinancialAccountType;

public record FinancialAccountResponse(
        UUID id,
        String name,
        FinancialAccountType type,
        String institution,
        String accountNumber,
        String currency,
        BigDecimal initialBalance,
        FinancialAccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static FinancialAccountResponse from(FinancialAccount account) {
        return new FinancialAccountResponse(account.getId(), account.getName(), account.getType(),
                account.getInstitution(), account.getAccountNumber(), account.getCurrency(),
                account.getInitialBalance(), account.getStatus(), account.getCreatedAt(),
                account.getUpdatedAt(), account.getDeletedAt());
    }
}
