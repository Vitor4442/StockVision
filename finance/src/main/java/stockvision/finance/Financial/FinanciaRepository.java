package stockvision.finance.Financial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinanciaRepository extends JpaRepository<FinancialEntity, UUID>, JpaSpecificationExecutor<FinancialEntity> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from FinancialEntity f where f.id = :id")
    Optional<FinancialEntity> findByIdForUpdate(@Param("id") UUID id);
}
