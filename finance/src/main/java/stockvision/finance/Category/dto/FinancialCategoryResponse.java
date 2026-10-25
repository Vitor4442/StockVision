package stockvision.finance.Category.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import stockvision.finance.Category.FinancialCategory;
import stockvision.finance.Category.FinancialCategoryStatus;
import stockvision.finance.Financial.FinancialType;

public record FinancialCategoryResponse(
        UUID id,
        String name,
        String description,
        FinancialType type,
        FinancialCategoryStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static FinancialCategoryResponse from(FinancialCategory category) {
        return new FinancialCategoryResponse(category.getId(), category.getName(), category.getDescription(),
                category.getType(), category.getStatus(), category.getCreatedAt(), category.getUpdatedAt(),
                category.getDeletedAt());
    }
}
