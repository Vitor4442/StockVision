package stockvision.company.company.dto;

import java.time.LocalDateTime;

public record CompanyResponse(Long id, String legalName, String tradeName, String cnpj,
                              String email, String phone, String address, boolean active,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {}
