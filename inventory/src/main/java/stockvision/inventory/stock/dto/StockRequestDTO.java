package stockvision.inventory.stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record StockRequestDTO(
        @NotNull Long productId,
        @NotNull Long locationId,
        @NotNull @PositiveOrZero BigDecimal quantity
) {
}
