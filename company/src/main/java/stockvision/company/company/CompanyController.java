package stockvision.company.company;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockvision.company.company.dto.CompanyRequest;
import stockvision.company.company.dto.CompanyResponse;

@RestController
@RequestMapping("/v1/companies")
public class CompanyController {
    private final CompanyService service;

    public CompanyController(CompanyService service) { this.service = service; }

    @GetMapping
    public CompanyResponse getCompany() { return service.getCompany(); }

    @PutMapping
    public CompanyResponse saveCompany(@RequestBody @Valid CompanyRequest request) {
        return service.save(request);
    }
}
