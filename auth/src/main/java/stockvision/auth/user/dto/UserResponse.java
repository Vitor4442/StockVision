package stockvision.auth.user.dto;

import stockvision.auth.user.User;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        boolean active,
        OffsetDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
