package stockvision.procurement.purchase_orders.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProcurementAproveEvent(
        BigDecimal totalAmount,
        String notes,
        LocalDateTime expectedDeliveryAt,
        LocalDateTime orderedAt
) {
}
