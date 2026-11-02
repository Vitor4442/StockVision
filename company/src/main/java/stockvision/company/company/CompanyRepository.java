package stockvision.company.company;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    java.util.Optional<Company> findFirstByOrderByIdAsc();
    boolean existsByCnpjAndIdNot(String cnpj, Long id);
}
