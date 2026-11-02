package stockvision.company.company;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import stockvision.company.company.dto.CompanyRequest;
import stockvision.company.company.dto.CompanyResponse;

@Service
public class CompanyService {
    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public CompanyResponse getCompany() {
        return repository.findFirstByOrderByIdAsc().map(this::toResponse).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Os dados da empresa ainda não foram cadastrados"));
    }

    @Transactional
    public CompanyResponse save(CompanyRequest request) {
        Company company = repository.findFirstByOrderByIdAsc().orElse(null);
        if (company != null && repository.existsByCnpjAndIdNot(request.cnpj(), company.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma empresa com este CNPJ");
        if (company == null) company = new Company(request.legalName(), request.tradeName(), request.cnpj(),
                request.email(), request.phone(), request.address());
        else {
            company.setLegalName(request.legalName());
            company.setTradeName(request.tradeName());
            company.setCnpj(request.cnpj());
            company.setEmail(request.email());
            company.setPhone(request.phone());
            company.setAddress(request.address());
        }
        return toResponse(repository.save(company));
    }

    private CompanyResponse toResponse(Company c) {
        return new CompanyResponse(c.getId(), c.getLegalName(), c.getTradeName(), c.getCnpj(), c.getEmail(),
                c.getPhone(), c.getAddress(), c.isActive(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
