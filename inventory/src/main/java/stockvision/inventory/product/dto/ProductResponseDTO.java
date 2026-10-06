package stockvision.inventory.product.dto;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String sku,
        String description,
        Boolean active,
        BigDecimal price,
        BigDecimal costPrice
) {
}
