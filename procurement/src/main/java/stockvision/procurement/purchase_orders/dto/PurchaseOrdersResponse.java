package stockvision.procurement.purchase_orders.dto;

import stockvision.procurement.purchase_orders.PurchaseOrders;
import stockvision.procurement.purchase_orders.PurchaseOrdersStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseOrdersResponse(
        Long id,
        String orderNumber,
        PurchaseOrdersStatus status,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal shippingCost,
        BigDecimal totalAmount,
        String notes,
        LocalDateTime expectedDeliveryAt,
        LocalDateTime orderedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PurchaseOrdersResponse from(PurchaseOrders order) {
        return new PurchaseOrdersResponse(
                order.getId(), order.getOrderNumber(), order.getStatus(), order.getSubtotal(),
                order.getDiscount(), order.getShippingCost(), order.getTotalAmount(), order.getNotes(),
                order.getExpectedDeliveryAt(), order.getOrderedAt(), order.getCreatedAt(), order.getUpdatedAt()
        );
    }
}
