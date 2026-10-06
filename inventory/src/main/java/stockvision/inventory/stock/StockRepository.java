package stockvision.inventory.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductIdAndLocationId(Long productId, Long locationId);
    Optional<List<Stock>> findByLocationId(Long locationId);
}
