package stockvision.procurement.purchase_orders;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders, Long> {
}
