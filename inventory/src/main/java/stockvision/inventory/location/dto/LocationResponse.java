package stockvision.inventory.location.dto;

public record LocationResponse(
        Long id,
        String name,
        String description,
        boolean active
) {
}
