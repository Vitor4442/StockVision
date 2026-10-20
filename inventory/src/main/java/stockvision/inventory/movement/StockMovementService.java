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
import stockvision.inventory.event.StockEventPublisher;
import stockvision.inventory.event.StockLowEvent;
import stockvision.inventory.location.Location;
import stockvision.inventory.location.LocationRepository;
import stockvision.inventory.movement.dto.StockMovementRequestDTO;
import stockvision.inventory.movement.dto.StockMovementTrackingCodeRequestDTO;
import stockvision.inventory.product.Product;
import stockvision.inventory.product.ProductRepository;
import stockvision.inventory.shared.exception.ResourceNotFoundException;
import stockvision.inventory.stock.Stock;
import stockvision.inventory.stock.StockMapper;
import stockvision.inventory.stock.StockRepository;
import stockvision.inventory.stock.dto.StockResponseDTO;
import stockvision.inventory.stock.dto.UpdateStockQuantityDTO;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementService {

    private final StockMovementMapper mapper;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final StockRepository stockRepository;
    private final StockEventPublisher stockEventPublisher;
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
        verifyStock(stock);
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

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeQuantityByTrackingCode(String trackingCode, StockMovementTrackingCodeRequestDTO request) {
        Stock stock = findByTrackingCode(trackingCode);
        stock.removeStock(request.quantity());
        saveWithOptimisticLockCheck(stock);
        verifyStock(stock);
        stockMovementRepository.save(createTrackingCodeMovement(stock, request, StockMovementType.EXIT));
    }

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addQuantityByTrackingCode(String trackingCode, StockMovementTrackingCodeRequestDTO request) {
        Stock stock = findByTrackingCode(trackingCode);
        stock.addStock(request.quantity());
        saveWithOptimisticLockCheck(stock);
        stockMovementRepository.save(createTrackingCodeMovement(stock, request, StockMovementType.ENTRY));
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

    private Stock findByTrackingCode(String trackingCode) {
        return stockRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para o código informado."));
    }

    private StockMovement createTrackingCodeMovement(
            Stock stock,
            StockMovementTrackingCodeRequestDTO request,
            StockMovementType type
    ) {
        return StockMovement.builder()
                .product(stock.getProduct())
                .location(stock.getLocation())
                .type(type)
                .quantity(request.quantity())
                .source(StockMovementSource.MANUAL)
                .referenceId(request.referenceId())
                .build();
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

    private void verifyStock(Stock stock) {
        if (stock.getQuantity().compareTo(stock.getMinimumQuantity()) <= 0) {
            StockLowEvent event = new StockLowEvent(
                    UUID.randomUUID(),
                    stock.getProduct().getId(),
                    stock.getLocation().getId(),
                    stock.getQuantity(),
                    stock.getMinimumQuantity()
            );

            stockEventPublisher.publishStockLow(event);
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
