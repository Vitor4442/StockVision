package stockvision.inventory.movement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record StockMovementTrackingCodeRequestDTO(
        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        BigDecimal quantity,

        String referenceId
) {
}
