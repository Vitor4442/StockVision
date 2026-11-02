package stockvision.company.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompanyRequest(
        @NotBlank @Size(max = 160) String legalName,
        @Size(max = 160) String tradeName,
        @NotBlank @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 números") String cnpj,
        @Email @Size(max = 160) String email,
        @Size(max = 30) String phone,
        @Size(max = 255) String address
) {}
