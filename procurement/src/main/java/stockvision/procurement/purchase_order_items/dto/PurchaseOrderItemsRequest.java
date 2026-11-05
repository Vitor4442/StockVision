package stockvision.procurement.purchase_order_items.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PurchaseOrderItemsRequest(
        @NotNull @Positive Long purchaseOrderId,
        @NotNull @Positive Long productId,
        @NotNull @Positive BigDecimal quantity
) {}
