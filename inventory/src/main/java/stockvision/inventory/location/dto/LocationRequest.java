package stockvision.inventory.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationRequest(

        @NotBlank
        String name,

        String description
) {
}
