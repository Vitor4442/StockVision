package stockvision.inventory.stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateStockQuantityDTO(@NotNull @Positive BigDecimal quantity) {
}
