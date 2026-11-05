package stockvision.procurement.client.dto;

import java.math.BigDecimal;

public record InventoryProductResponse(
        Long id,
        BigDecimal costPrice
) {
}
