package stockvision.finance.Category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import stockvision.finance.Financial.FinancialType;

public record FinancialCategoryRequest(
        @NotBlank String name,
        String description,
        @NotNull FinancialType type
) {}
