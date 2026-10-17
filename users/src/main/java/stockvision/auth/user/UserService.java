package stockvision.auth.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void toggleNotification() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        user.changeNotification();
    }

    @Transactional(readOnly = true)
    public List<String> findNotificationRecipientEmails() {
        return userRepository.findByNotificationTrue()
                .stream()
                .map(User::getEmail)
                .toList();
    }
}
