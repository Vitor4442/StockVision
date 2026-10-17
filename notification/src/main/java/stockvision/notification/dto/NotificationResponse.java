package stockvision.notification.dto;

import stockvision.notification.Notification;
import stockvision.notification.enums.NotificationChannel;
import stockvision.notification.enums.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        String type,
        String title,
        String message,
        NotificationStatus status,
        NotificationChannel channel,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getChannel(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}