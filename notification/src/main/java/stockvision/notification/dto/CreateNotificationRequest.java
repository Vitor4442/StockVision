package stockvision.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import stockvision.notification.enums.NotificationChannel;

import java.util.UUID;

public record CreateNotificationRequest(
        @NotNull UUID userId,

        @NotBlank
        @Size(max = 100)
        String type,

        @NotBlank
        @Size(max = 200)
        String title,

        @NotBlank
        @Size(max = 2000)
        String message,

        @NotNull
        NotificationChannel channel
) {}