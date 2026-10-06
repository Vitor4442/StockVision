package stockvision.inventory.movement.dto;

import stockvision.inventory.movement.StockMovementSource;
import stockvision.inventory.movement.StockMovementType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record StockMovementResponseDTO(
        Long id,
        Long productId,
        Long locationId,
        StockMovementType type,
        BigDecimal quantity,
        StockMovementSource source,
        String referenceId,
        OffsetDateTime createdAt
) {
}