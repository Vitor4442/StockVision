package stockvision.procurement.purchase_orders;

import jakarta.persistence.*;
import lombok.Data;
import stockvision.procurement.purchase_order_items.PurhaseOrderItems;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class PurchaseOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_orders_seq_gen")
    @SequenceGenerator(name = "purchase_orders_seq_gen", sequenceName = "purchase_orders_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "order_number", nullable = false, length = 30, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PurchaseOrdersStatus status;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount", precision = 15, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "shipping_cost", precision = 15, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "expected_delivery_at")
    private LocalDateTime expectedDeliveryAt;

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "purchaseOrders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurhaseOrderItems> items = new ArrayList<>();

}
