package stockvision.inventory.stock;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import stockvision.inventory.location.Location;
import stockvision.inventory.location.LocationRepository;
import stockvision.inventory.product.Product;
import stockvision.inventory.product.ProductRepository;
import stockvision.inventory.stock.dto.StockRequestDTO;
import stockvision.inventory.stock.dto.StockResponseDTO;
import stockvision.inventory.stock.dto.UpdateStockQuantityDTO;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<StockResponseDTO> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable)
                .map(stockMapper::toDto);
    }

    @Transactional(readOnly = true)
    public StockResponseDTO getByProductAndLocation(Long productId, Long locationId) {
        Stock stock = findStock(productId, locationId);
        return stockMapper.toDto(stock);
    }

    @Transactional(readOnly = true)
    public List<StockResponseDTO> getByLocation(Long locationId) {
        List<Stock> stock = stockRepository.findByLocationId(locationId).orElseThrow(() -> new EntityNotFoundException("Local não encontrado"));
        return  stock.stream()
                .map(stockMapper::toDto).toList();
    }

    @Transactional
    public StockResponseDTO create(StockRequestDTO dto) {

       Location location = locationRepository.findById(dto.locationId()).orElseThrow(() -> new RuntimeException("Galpão não encontrado"));
       Product product = productRepository.findById(dto.productId()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        stockRepository.findByProductIdAndLocationId(dto.productId(), dto.locationId()).ifPresent(s -> {
                    throw new IllegalArgumentException("Registro de estoque já existe para este produto e local.");
                });

        Stock stock = stockMapper.toEntity(dto);
        stock.setProduct(product);
        stock.setLocation(location);
        Stock savedStock = stockRepository.save(stock);
        return stockMapper.toDto(savedStock);
    }

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2) // Aguarda 100ms, depois 200ms...
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StockResponseDTO addQuantity(Long productId, Long locationId, UpdateStockQuantityDTO dto) {
        Stock stock = findStock(productId, locationId);
        stock.setQuantity(stock.getQuantity().add(dto.quantity()));

        return saveWithOptimisticLockCheck(stock);
    }

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2) // Aguarda 100ms, depois 200ms...
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StockResponseDTO removeQuantity(Long productId, Long locationId, UpdateStockQuantityDTO dto) {

        Stock stock = stockRepository.findByProductIdAndLocationId(productId, locationId).orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado."));

        if (stock.getQuantity().compareTo(dto.quantity()) < 0) {
            throw new IllegalArgumentException("Estoque insuficiente.");
        }

        stock.setQuantity(stock.getQuantity().subtract(dto.quantity()));

        Stock updatedStock = stockRepository.saveAndFlush(stock);
        return stockMapper.toDto(updatedStock);
    }

    private Stock findStock(Long productId, Long locationId) {
        return stockRepository.findByProductIdAndLocationId(productId, locationId).orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado para o produto e local informados."));
    }

    private List<Stock> findStockByLocation(Long locationId){
        return stockRepository.findByLocationId(locationId).orElseThrow(() -> new RuntimeException("Local não encontrado"));
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
    public StockResponseDTO recoverRemoveQuantity(
            ObjectOptimisticLockingFailureException e,
            Long productId,
            Long locationId,
            UpdateStockQuantityDTO dto) {

        log.error("Excedido o número de tentativas para o produto {} no local {}", productId, locationId, e);
        throw new IllegalStateException("O sistema está sob alta carga. Não foi possível atualizar o estoque. Tente novamente.", e);
    }

}
