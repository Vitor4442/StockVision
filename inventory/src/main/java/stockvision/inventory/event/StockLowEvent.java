package stockvision.inventory.event;


import java.math.BigDecimal;
import java.util.UUID;

public record StockLowEvent(
        UUID eventId,
        Long productId,
        Long locationId,
        BigDecimal currentQuantity,
        BigDecimal minimumQuantity
) {
}