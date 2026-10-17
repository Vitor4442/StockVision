package stockvision.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import stockvision.notification.enums.NotificationChannel;
import stockvision.notification.enums.NotificationStatus;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationStatusService {

    private static final int MAX_ERROR_LENGTH = 2000;

    private final NotificationRepository notificationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Notification createPendingStockLow(UUID eventId, String message) {
        if (eventId != null) {
            var existingNotification = notificationRepository.findByEventId(eventId);
            if (existingNotification.isPresent()) {
                return existingNotification.get();
            }
        }

        return notificationRepository.save(Notification.builder()
                .eventId(eventId)
                .type("STOCK_LOW")
                .title("Alerta de estoque baixo")
                .message(message)
                .status(NotificationStatus.PENDING)
                .channel(NotificationChannel.EMAIL)
                .attemptCount(0)
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markProcessing(UUID notificationId) {
        Notification notification = findNotification(notificationId);
        if (notification.getStatus() != NotificationStatus.PENDING && notification.getStatus() != NotificationStatus.FAILED && notification.getStatus() != NotificationStatus.PROCESSING) {
            throw new IllegalStateException("Não é possível processar uma notificação em status " + notification.getStatus());
        }
        notification.setStatus(NotificationStatus.PROCESSING);
        notification.setAttemptCount(notification.getAttemptCount() + 1);
        notification.setLastError(null);
        notificationRepository.save(notification);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSent(UUID notificationId) {
        Notification notification = findNotification(notificationId);
        requireProcessing(notification);
        notification.setStatus(NotificationStatus.SENT);
        notification.setLastError(null);
        notificationRepository.save(notification);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID notificationId, String error) {
        Notification notification = findNotification(notificationId);
        requireProcessing(notification);
        notification.setStatus(NotificationStatus.FAILED);
        notification.setLastError(truncate(error));
        notificationRepository.save(notification);
    }

    private Notification findNotification(UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notificação não encontrada: " + notificationId));
    }

    private void requireProcessing(Notification notification) {
        if (notification.getStatus() != NotificationStatus.PROCESSING) {
            throw new IllegalStateException("A notificação precisa estar PROCESSING para mudar para "
                    + "SENT ou FAILED; status atual: " + notification.getStatus());
        }
    }

    private String truncate(String error) {
        if (error == null || error.isBlank()) {
            return "Falha ao enviar e-mail";
        }
        return error.length() <= MAX_ERROR_LENGTH ? error : error.substring(0, MAX_ERROR_LENGTH);
    }
}
