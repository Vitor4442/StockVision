package stockvision.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stockvision.notification.enums.NotificationStatus;
import stockvision.notification.event.StockLowEvent;
import stockvision.notification.service.EmailService;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final NotificationStatusService notificationStatusService;

    public void processStockLow(StockLowEvent event) {
        String message = """
                O estoque atingiu o limite mínimo.

                Produto: %d
                Local: %d
                Quantidade atual: %s
                Quantidade mínima: %s
                """.formatted(
                event.productId(),
                event.locationId(),
                event.currentQuantity().toPlainString(),
                event.minimumQuantity().toPlainString()
        );

        Notification notification = notificationStatusService.createPendingStockLow(event.eventId(), message);
        if (notification.getStatus() == NotificationStatus.SENT) {
            return;
        }

        notificationStatusService.markProcessing(notification.getId());

        try {
            emailService.sendStockLowEmail(event);
        } catch (RuntimeException exception) {
            try {
                notificationStatusService.markFailed(notification.getId(), exception.getMessage());
            } catch (RuntimeException statusException) {
                exception.addSuppressed(statusException);
            }
            throw exception;
        }

        notificationStatusService.markSent(notification.getId());
    }
}
