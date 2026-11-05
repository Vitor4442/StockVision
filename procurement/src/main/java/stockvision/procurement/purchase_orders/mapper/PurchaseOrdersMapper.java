package stockvision.procurement.purchase_orders.mapper;

import org.springframework.stereotype.Component;
import stockvision.procurement.purchase_orders.PurchaseOrders;
import stockvision.procurement.purchase_orders.dto.PurchaseOrdersRequest;
import stockvision.procurement.purchase_orders.dto.PurchaseOrdersResponse;

import java.math.BigDecimal;

@Component
public class PurchaseOrdersMapper {

    public PurchaseOrders toEntity(PurchaseOrdersRequest request) {
        PurchaseOrders order = new PurchaseOrders();
        order.setOrderNumber(request.orderNumber());
        order.setStatus(request.status());
        order.setSubtotal(request.subtotal());
        order.setDiscount(request.discount() != null ? request.discount() : BigDecimal.ZERO);
        order.setShippingCost(request.shippingCost() != null ? request.shippingCost() : BigDecimal.ZERO);
        order.setNotes(request.notes());
        order.setExpectedDeliveryAt(request.expectedDeliveryAt());
        order.setOrderedAt(request.orderedAt());
        return order;
    }

    public PurchaseOrdersResponse toResponse(PurchaseOrders order) {
        return PurchaseOrdersResponse.from(order);
    }
}
