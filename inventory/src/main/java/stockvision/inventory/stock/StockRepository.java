package stockvision.inventory.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductIdAndLocationId(Long productId, Long locationId);
    Optional<Stock> findByTrackingCode(String trackingCode);
    Optional<List<Stock>> findByLocationId(Long locationId);

    @Query(value = """
    SELECT COALESCE(SUM(s.quantity * p.price), 0)
    FROM stocks s
    INNER JOIN products p ON s.product_id = p.id
    WHERE s.location_id = :locationId
    """, nativeQuery = true)
    BigDecimal findTotalByLocationId(@Param("locationId") Long locationId);
}
