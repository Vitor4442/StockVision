package stockvision.procurement.purchase_order_items.dto;

import stockvision.procurement.purchase_order_items.PurhaseOrderItems;

import java.math.BigDecimal;

public record PurchaseOrderItemsResponse(
        Long id,
        Long purchaseOrderId,
        Long productId,
        BigDecimal quantity
) {
    public static PurchaseOrderItemsResponse from(PurhaseOrderItems item) {
        return new PurchaseOrderItemsResponse(
                item.getId(),
                item.getPurchaseOrders() == null ? null : item.getPurchaseOrders().getId(),
                item.getProductId(),
                item.getQuantity()
        );
    }
}
