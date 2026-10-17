package stockvision.auth.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me/notifications")
    public ResponseEntity<Void> toggleNotification() {
        userService.toggleNotification();
        return ResponseEntity.noContent().build();
    }

}
