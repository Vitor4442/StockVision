package stockvision.procurement.purchase_order_items.mapper;

import org.springframework.stereotype.Component;
import stockvision.procurement.purchase_order_items.PurhaseOrderItems;
import stockvision.procurement.purchase_order_items.dto.PurchaseOrderItemsRequest;
import stockvision.procurement.purchase_order_items.dto.PurchaseOrderItemsResponse;
import stockvision.procurement.purchase_orders.PurchaseOrders;

@Component
public class PurchaseOrderItemsMapper {

    public PurhaseOrderItems toEntity(PurchaseOrderItemsRequest request, PurchaseOrders order) {
        PurhaseOrderItems item = new PurhaseOrderItems();
        item.setPurchaseOrders(order);
        item.setProductId(request.productId());
        item.setQuantity(request.quantity());
        return item;
    }

    public PurchaseOrderItemsResponse toResponse(PurhaseOrderItems item) {
        return PurchaseOrderItemsResponse.from(item);
    }
}
