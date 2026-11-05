package stockvision.procurement.purchase_order_items;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderItemsRepository extends JpaRepository<PurhaseOrderItems, Long> {
    List<PurhaseOrderItems> findByPurchaseOrdersId(Long purchaseOrderId);
}
