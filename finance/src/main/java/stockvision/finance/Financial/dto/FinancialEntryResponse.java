package stockvision.finance.Financial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import stockvision.finance.Financial.FinancialEntity;
import stockvision.finance.Financial.FinancialSourceType;
import stockvision.finance.Financial.FinancialStatus;
import stockvision.finance.Financial.FinancialType;

public record FinancialEntryResponse(
        UUID id,
        FinancialType type,
        String description,
        BigDecimal amount,
        LocalDate issueDate,
        LocalDate dueDate,
        FinancialStatus status,
        UUID categoryId,
        UUID counterpartyId,
        FinancialSourceType sourceType,
        UUID sourceId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static FinancialEntryResponse from(FinancialEntity entity) {
        return new FinancialEntryResponse(entity.getId(), entity.getType(), entity.getDescription(),
                entity.getAmount(), entity.getIssueDate(), entity.getDueDate(), entity.getStatus(),
                entity.getCategoryId(), entity.getCounterpartyId(), entity.getSourceType(), entity.getSourceId(),
                entity.getCreatedAt(), entity.getUpdatedAt(), entity.getDeletedAt());
    }
}
