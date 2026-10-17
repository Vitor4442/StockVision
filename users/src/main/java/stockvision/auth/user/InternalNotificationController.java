package stockvision.auth.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
@RequestMapping("/internal/notifications")
public class InternalNotificationController {

    private final UserService userService;
    private final String internalToken;

    public InternalNotificationController(UserService userService, @Value("${app.internal.token:}") String internalToken) {
        this.userService = userService;
        this.internalToken = internalToken;
    }

    @GetMapping("/recipients")
    public List<String> findRecipients(@RequestHeader(name = "X-Internal-Token", required = false) String providedToken) {
        if (internalToken.isBlank() || providedToken == null || !MessageDigest.isEqual(internalToken.getBytes(StandardCharsets.UTF_8), providedToken.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return userService.findNotificationRecipientEmails();
    }
}
