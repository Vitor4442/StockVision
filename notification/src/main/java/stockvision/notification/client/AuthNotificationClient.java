package stockvision.notification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${app.auth.base-url}")
public interface AuthNotificationClient {

    @GetMapping("/internal/notifications/recipients")
    String[] findRecipients(@RequestHeader("X-Internal-Token") String internalToken);
}
