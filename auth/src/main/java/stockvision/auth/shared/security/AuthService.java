package stockvision.auth.shared.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import stockvision.auth.user.User;
import stockvision.auth.user.UserRepository;
import stockvision.auth.user.dto.LoginRequest;
import stockvision.auth.user.dto.LoginResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public LoginResponse login(LoginRequest request) {
        String email = request.email()
                .trim()
                .toLowerCase();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos"));

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        }

        String token = jwtService.generateToken(user.getId());

        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs() / 1000);
    }
}
