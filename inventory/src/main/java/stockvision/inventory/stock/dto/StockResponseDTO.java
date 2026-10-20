package stockvision.inventory.stock.dto;

import java.math.BigDecimal;

public record StockResponseDTO(
        Long id,
        Long productId,
        Long locationId,
        BigDecimal quantity,
        String trackingCode,
        Long version
) {
}
