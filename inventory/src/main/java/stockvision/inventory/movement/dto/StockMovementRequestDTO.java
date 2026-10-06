package stockvision.inventory.movement.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record StockMovementRequestDTO(
        @NotNull(message = "O ID do produto é obrigatório")
        Long productId,

        @NotNull(message = "O ID da localização é obrigatório")
        Long locationId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        BigDecimal quantity,

        String referenceId
) {
}
