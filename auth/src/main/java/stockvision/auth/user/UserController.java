package stockvision.auth.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockvision.auth.shared.security.AuthService;
import stockvision.auth.user.dto.LoginRequest;
import stockvision.auth.user.dto.LoginResponse;
import stockvision.auth.user.dto.RegisterRequest;
import stockvision.auth.user.dto.UserResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PatchMapping("/changeNotifications")
    public ResponseEntity<Void> toggleNotification() {
        userService.toggleNotification();
        return ResponseEntity.noContent().build();
    }

}
