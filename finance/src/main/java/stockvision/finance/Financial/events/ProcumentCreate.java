package stockvision.finance.Financial.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProcumentCreate(
        BigDecimal totalAmount,
        String notes,
        LocalDateTime expectedDeliveryAt,
        LocalDateTime orderedAt
) {}