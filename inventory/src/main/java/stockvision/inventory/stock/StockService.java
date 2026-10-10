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
import stockvision.inventory.shared.exception.DuplicateStockProductAndLocation;
import stockvision.inventory.shared.exception.ResourceNotFoundException;
import stockvision.inventory.stock.dto.StockRequestDTO;
import stockvision.inventory.stock.dto.StockResponseDTO;
import stockvision.inventory.stock.dto.UpdateStockQuantityDTO;

import java.util.Collections;
import java.util.DuplicateFormatFlagsException;
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
        List<Stock> stock = stockRepository.findByLocationId(locationId).orElseThrow(() -> new ResourceNotFoundException("Local não encontrado"));
        return  stock.stream()
                .map(stockMapper::toDto).toList();
    }

    @Transactional
    public StockResponseDTO create(StockRequestDTO dto) {

       Location location = locationRepository.findById(dto.locationId()).orElseThrow(() -> new ResourceNotFoundException("Galpão não encontrado"));
       Product product = productRepository.findById(dto.productId()).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        stockRepository.findByProductIdAndLocationId(dto.productId(), dto.locationId()).ifPresent(s -> {
            throw new DuplicateStockProductAndLocation("Já possui um local com esses pedidos");
                });

        Stock stock = stockMapper.toEntity(dto);
        stock.setProduct(product);
        stock.setLocation(location);
        Stock savedStock = stockRepository.save(stock);
        return stockMapper.toDto(savedStock);
    }

    private Stock findStock(Long productId, Long locationId) {
        return stockRepository.findByProductIdAndLocationId(productId, locationId).orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para o produto e local informados."));
    }

}
