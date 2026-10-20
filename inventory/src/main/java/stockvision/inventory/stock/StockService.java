package stockvision.inventory.stock;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stockvision.inventory.location.Location;
import stockvision.inventory.location.LocationRepository;
import stockvision.inventory.product.Product;
import stockvision.inventory.product.ProductRepository;
import stockvision.inventory.shared.exception.DuplicateStockProductAndLocation;
import stockvision.inventory.shared.exception.ResourceNotFoundException;
import stockvision.inventory.stock.dto.StockRequestDTO;
import stockvision.inventory.stock.dto.StockResponseDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final BarcodeService barcodeService;

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
    public StockResponseDTO getByTrackingCode(String trackingCode) {
        Stock stock = stockRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para o código informado."));
        return stockMapper.toDto(stock);
    }

    @Transactional
    public byte[] generateBarcode(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado com id: " + stockId));

        if (stock.getTrackingCode() == null) {
            stock.generateTrackingCode();
            stockRepository.save(stock);
        }

        try {
            BufferedImage barcode = barcodeService.generateCode(stock.getTrackingCode());
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                if (!ImageIO.write(barcode, "PNG", output)) {
                    throw new IllegalStateException("Não foi possível codificar o código de barras como PNG.");
                }
                return output.toByteArray();
            }
        } catch (WriterException | IOException exception) {
            throw new IllegalStateException("Não foi possível gerar o código de barras do estoque.", exception);
        }
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
        stock.generateTrackingCode();
        Stock savedStock = stockRepository.save(stock);
        return stockMapper.toDto(savedStock);
    }

    private Stock findStock(Long productId, Long locationId) {
        return stockRepository.findByProductIdAndLocationId(productId, locationId).orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para o produto e local informados."));
    }

}
