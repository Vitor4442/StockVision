package stockvision.inventory.stock.dto;

import org.springframework.retry.annotation.Recover;
import stockvision.inventory.location.Location;
import stockvision.inventory.product.Product;

import java.math.BigDecimal;

@Recover
public record StockResponseDTO(
        Long id,
        Long productId,
        Long locationId,
        BigDecimal quantity,
        Long version
) {
}
