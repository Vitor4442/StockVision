package stockvision.finance.Category;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FinancialCategoryRepository extends JpaRepository<FinancialCategory, UUID>, JpaSpecificationExecutor<FinancialCategory> {
    boolean existsByNameIgnoreCaseAndTypeAndDeletedAtIsNull(String name, stockvision.finance.Financial.FinancialType type);

    boolean existsByNameIgnoreCaseAndTypeAndDeletedAtIsNullAndIdNot(String name, stockvision.finance.Financial.FinancialType type, UUID id);
}
