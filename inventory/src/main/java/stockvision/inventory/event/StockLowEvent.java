package stockvision.inventory.event;


import java.math.BigDecimal;

public record StockLowEvent(
        Long productId,
        Long locationId,
        BigDecimal currentQuantity,
        BigDecimal minimumQuantity
) {
}