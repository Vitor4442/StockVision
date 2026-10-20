package stockvision.inventory.product;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stockvision.inventory.product.dto.ProductRequestDTO;
import stockvision.inventory.product.dto.ProductResponseDTO;
import stockvision.inventory.shared.exception.DuplicateSkuException;
import stockvision.inventory.shared.exception.NotFoundTrackingCode;
import stockvision.inventory.shared.exception.ResourceNotFoundException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BarcodeService barcodeService;

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
        return productMapper.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        if(productRepository.existsBySku(dto.sku())){
            throw new DuplicateSkuException("Ja existe produto com esse SKU: " + dto.sku());
        }

        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return productMapper.toResponseDTO(saved);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {

        if(productRepository.existsBySku(dto.sku())){
            throw new DuplicateSkuException("Ja existe produto com esse SKU: " + dto.sku());
        }

        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));

        if (!product.getSku().equalsIgnoreCase(dto.sku()) && productRepository.existsBySku(dto.sku())) {
            throw new DuplicateSkuException("O novo SKU informado já pertence a outro produto: " + dto.sku());
        }

        productMapper.updateEntityFromDto(dto, product);
        Product updated = productRepository.save(product);
        return productMapper.toResponseDTO(updated);
    }

    @Transactional
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));

        product.setActive(true);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchBySKU(String sku, Pageable pageable) {
        if (sku == null || sku.isBlank()) {
            return productRepository.findAll(pageable).map(productMapper::toResponseDTO);
        }

        return productRepository.findBySkuContainingIgnoreCase(sku, pageable).map(productMapper::toResponseDTO);
    }

    @Transactional
    public String generateCode(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
        return product.generateTrackingCode();
    }

    public byte[] getBarcode(Long id) throws WriterException, IOException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
        if(product.getTrackingCode() == null){
            new NotFoundTrackingCode("Produto não possui código, por favor gere um código");
        }
        String trackingCode = product.getTrackingCode();

        BufferedImage image = barcodeService.generateCode(trackingCode);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);

        return output.toByteArray();
    }


}