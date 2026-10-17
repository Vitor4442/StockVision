package stockvision.notification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "users-service", url = "${app.users.base-url}")
public interface UsersNotificationClient {

    @GetMapping("/internal/notifications/recipients")
    String[] findRecipients(@RequestHeader("X-Internal-Token") String internalToken);
}
