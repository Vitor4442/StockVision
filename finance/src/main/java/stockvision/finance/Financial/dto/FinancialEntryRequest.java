package stockvision.finance.Financial.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import stockvision.finance.Financial.FinancialSourceType;

public record FinancialEntryRequest(
        @NotBlank String description,
        @NotNull @Positive BigDecimal amount,
        LocalDate issueDate,
        LocalDate dueDate,
        UUID categoryId,
        UUID counterpartyId,
        FinancialSourceType sourceType,
        UUID sourceId
) {}
