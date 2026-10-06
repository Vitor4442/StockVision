package stockvision.inventory.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stockvision.inventory.location.Location;
import stockvision.inventory.location.LocationRepository;
import stockvision.inventory.movement.dto.StockMovementRequestDTO;
import stockvision.inventory.product.Product;
import stockvision.inventory.product.ProductRepository;
import stockvision.inventory.shared.exception.ResourceNotFoundException;
import stockvision.inventory.stock.Stock;
import stockvision.inventory.stock.StockRepository;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementMapper mapper;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final StockRepository stockRepository;

    public void RemoveQuantity(StockMovementRequestDTO stockMovementRequestDTO){
        validate(stockMovementRequestDTO);
        Stock stock = stockRepository.findByProductIdAndLocationId(stockMovementRequestDTO.productId(), stockMovementRequestDTO.locationId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        stock.removeStock(stockMovementRequestDTO.quantity());
        StockMovement stockMovement = mapper.toEntity(stockMovementRequestDTO);
        stockMovement.setSource(StockMovementSource.MANUAL);
        stockMovement.setType(StockMovementType.ENTRY);
        stockMovement.setProduct(setProduct(stockMovementRequestDTO));
        stockMovement.setLocation(setLocation(stockMovementRequestDTO));
        stockMovementRepository.save(stockMovement);
    }

    public void AddQuantity(StockMovementRequestDTO stockMovementRequestDTO){
        validate(stockMovementRequestDTO);
        Stock stock = stockRepository.findByProductIdAndLocationId(stockMovementRequestDTO.productId(), stockMovementRequestDTO.locationId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        stock.addStock(stockMovementRequestDTO.quantity());
        StockMovement stockMovement = mapper.toEntity(stockMovementRequestDTO);
        stockMovement.setSource(StockMovementSource.MANUAL);
        stockMovement.setType(StockMovementType.ENTRY);
        stockMovement.setProduct(setProduct(stockMovementRequestDTO));
        stockMovement.setLocation(setLocation(stockMovementRequestDTO));
        stockMovementRepository.save(stockMovement);
    }

    private Product setProduct(StockMovementRequestDTO stockMovementRequestDTO) {
        return productRepository.findById(stockMovementRequestDTO.productId()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    private Location setLocation(StockMovementRequestDTO stockMovementRequestDTO) {
        return locationRepository.findById(stockMovementRequestDTO.locationId()).orElseThrow(() -> new RuntimeException("local não encontrado"));
    }

    private void validate(StockMovementRequestDTO stockMovementRequestDTO) {
        if (!locationRepository.existsById(stockMovementRequestDTO.locationId())) {
            throw new ResourceNotFoundException(
                    "Location not found: " + stockMovementRequestDTO.locationId()
            );
        }

        if (!productRepository.existsById(stockMovementRequestDTO.productId())) {
            throw new ResourceNotFoundException(
                    "Product not found: " + stockMovementRequestDTO.productId()
            );
        }
    }


}
