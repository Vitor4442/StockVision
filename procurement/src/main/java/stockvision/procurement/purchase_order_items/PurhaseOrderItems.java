package stockvision.procurement.purchase_order_items;

import jakarta.persistence.*;
import lombok.Data;
import stockvision.procurement.purchase_orders.PurchaseOrders;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")
@Data
public class PurhaseOrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_order_items_seq_gen")
    @SequenceGenerator(name = "purchase_order_items_seq_gen", sequenceName = "purchase_order_items_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrders purchaseOrders;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_value", nullable = false, precision = 38, scale = 2)
    private BigDecimal unitValue;

    @Column(name = "total_value", nullable = false, precision = 38, scale = 2)
    private BigDecimal totalValue;

}
