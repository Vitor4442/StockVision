package stockvision.inventory.movement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import stockvision.inventory.location.Location;
import stockvision.inventory.location.LocationRepository;
import stockvision.inventory.movement.dto.StockMovementRequestDTO;
import stockvision.inventory.product.Product;
import stockvision.inventory.product.ProductRepository;
import stockvision.inventory.shared.exception.ResourceNotFoundException;
import stockvision.inventory.stock.Stock;
import stockvision.inventory.stock.StockMapper;
import stockvision.inventory.stock.StockRepository;
import stockvision.inventory.stock.dto.StockResponseDTO;
import stockvision.inventory.stock.dto.UpdateStockQuantityDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementService {

    private final StockMovementMapper mapper;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2) // Aguarda 100ms, depois 200ms...
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void RemoveQuantity(StockMovementRequestDTO stockMovementRequestDTO){
        validate(stockMovementRequestDTO);
        Stock stock = stockRepository.findByProductIdAndLocationId(stockMovementRequestDTO.productId(), stockMovementRequestDTO.locationId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        stock.removeStock(stockMovementRequestDTO.quantity());
        saveWithOptimisticLockCheck(stock);
        StockMovement stockMovement = mapper.toEntity(stockMovementRequestDTO);
        stockMovement.setSource(StockMovementSource.MANUAL);
        stockMovement.setType(StockMovementType.EXIT);
        stockMovement.setProduct(setProduct(stockMovementRequestDTO));
        stockMovement.setLocation(setLocation(stockMovementRequestDTO));
        stockMovementRepository.save(stockMovement);
    }

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2) // Aguarda 100ms, depois 200ms...
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void AddQuantity(StockMovementRequestDTO stockMovementRequestDTO){
        validate(stockMovementRequestDTO);
        Stock stock = stockRepository.findByProductIdAndLocationId(stockMovementRequestDTO.productId(), stockMovementRequestDTO.locationId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        stock.addStock(stockMovementRequestDTO.quantity());
        saveWithOptimisticLockCheck(stock);
        StockMovement stockMovement = mapper.toEntity(stockMovementRequestDTO);
        stockMovement.setSource(StockMovementSource.MANUAL);
        stockMovement.setType(StockMovementType.ENTRY);
        stockMovement.setProduct(setProduct(stockMovementRequestDTO));
        stockMovement.setLocation(setLocation(stockMovementRequestDTO));
        stockMovementRepository.save(stockMovement);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void AdjustedQuantity(StockMovementRequestDTO stockMovementRequestDTO){
        validate(stockMovementRequestDTO);
        Stock stock = stockRepository.findByProductIdAndLocationId(stockMovementRequestDTO.productId(), stockMovementRequestDTO.locationId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        stock.adjustedStock(stockMovementRequestDTO.quantity());
        saveWithOptimisticLockCheck(stock);
        StockMovement stockMovement = mapper.toEntity(stockMovementRequestDTO);
        stockMovement.setSource(StockMovementSource.MANUAL);
        stockMovement.setType(StockMovementType.ADJUSTMENT);
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

    private StockResponseDTO saveWithOptimisticLockCheck(Stock stock) {
        try {
            // Ao salvar, o Hibernate compara o 'version' atual do banco com o objeto em memória
            Stock updatedStock = stockRepository.save(stock);
            return stockMapper.toDto(updatedStock);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new IllegalStateException("O estoque foi alterado por outro usuário simultaneamente. Tente novamente.", e);
        }
    }

    @Recover
    public StockResponseDTO recoverRemoveQuantity( ObjectOptimisticLockingFailureException e,  Long productId,  Long locationId, UpdateStockQuantityDTO dto) {
        log.error("Excedido o número de tentativas para o produto {} no local {}", productId, locationId, e);
        throw new IllegalStateException("O sistema está sob alta carga. Não foi possível atualizar o estoque. Tente novamente.", e);
    }


}
