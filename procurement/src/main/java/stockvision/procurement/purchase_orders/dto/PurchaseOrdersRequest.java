package stockvision.procurement.purchase_orders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import stockvision.procurement.purchase_orders.PurchaseOrdersStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseOrdersRequest(
        @NotBlank String orderNumber,
        @NotNull PurchaseOrdersStatus status,
        @PositiveOrZero BigDecimal discount,
        @PositiveOrZero BigDecimal shippingCost,
        String notes,
        LocalDateTime expectedDeliveryAt,
        LocalDateTime orderedAt
) {}
