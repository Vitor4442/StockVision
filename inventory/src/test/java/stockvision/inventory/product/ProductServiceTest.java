package stockvision.inventory.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import stockvision.inventory.product.dto.ProductRequestDTO;
import stockvision.inventory.product.dto.ProductResponseDTO;
import stockvision.inventory.shared.exception.DuplicateSkuException;
import stockvision.inventory.shared.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        product = new Product(
                1L,
                "Teclado Mecânico",
                "TEC-001",
                "Teclado RGB",
                true,
                new BigDecimal("350.00"),
                new BigDecimal("200.00")
        );

        requestDTO = new ProductRequestDTO(
                "Teclado Mecânico",
                "TEC-001",
                "Teclado RGB",
                true,
                new BigDecimal("350.00"),
                new BigDecimal("200.00")
        );

        responseDTO = new ProductResponseDTO(
                1L,
                "Teclado Mecânico",
                "TEC-001",
                "Teclado RGB",
                true,
                new BigDecimal("350.00"),
                new BigDecimal("200.00")
        );
    }

    @Nested
    @DisplayName("Testes do método findAll")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar uma página de produtos com sucesso")
        void shouldReturnPagedProductsSuccessfully() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(product));

            when(productRepository.findAll(pageable)).thenReturn(productPage);
            when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

            Page<ProductResponseDTO> result = productService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(responseDTO);

            verify(productRepository, times(1)).findAll(pageable);
            verify(productMapper, times(1)).toResponseDTO(product);
        }
    }

    @Nested
    @DisplayName("Testes do método findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar o produto DTO quando encontrar o ID informado")
        void shouldReturnProductWhenIdExists() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

            ProductResponseDTO result = productService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.sku()).isEqualTo("TEC-001");

            verify(productRepository, times(1)).findById(1L);
            verify(productMapper, times(1)).toResponseDTO(product);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não for encontrado")
        void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Produto não encontrado com id: 99");

            verify(productRepository, times(1)).findById(99L);
            verifyNoInteractions(productMapper);
        }
    }

    @Nested
    @DisplayName("Testes do método createProduct")
    class CreateProductTests {

        @Test
        @DisplayName("Deve criar e retornar o produto com sucesso")
        void shouldCreateProductSuccessfully() {
            when(productMapper.toEntity(requestDTO)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

            ProductResponseDTO result = productService.createProduct(requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.sku()).isEqualTo("TEC-001");

            verify(productMapper, times(1)).toEntity(requestDTO);
            verify(productRepository, times(1)).save(product);
            verify(productMapper, times(1)).toResponseDTO(product);
        }
    }

    @Nested
    @DisplayName("Testes do método updateProduct")
    class UpdateProductTests {

        @Test
        @DisplayName("Deve atualizar o produto quando o SKU for o mesmo do produto original")
        void shouldUpdateProductWhenSkuIsNotChanged() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            doNothing().when(productMapper).updateEntityFromDto(requestDTO, product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

            ProductResponseDTO result = productService.updateProduct(1L, requestDTO);

            assertThat(result).isNotNull();

            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, never()).existsBySku(any());
            verify(productMapper, times(1)).updateEntityFromDto(requestDTO, product);
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Deve atualizar o produto quando o SKU for alterado e o novo SKU for único")
        void shouldUpdateProductWhenSkuIsChangedAndUnique() {
            ProductRequestDTO updatedDto = new ProductRequestDTO(
                    "Teclado Mecânico",
                    "TEC-NEW-002",
                    "Teclado RGB",
                    true,
                    new BigDecimal("350.00"),
                    new BigDecimal("200.00")
            );

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.existsBySku("TEC-NEW-002")).thenReturn(false);
            doNothing().when(productMapper).updateEntityFromDto(updatedDto, product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

            ProductResponseDTO result = productService.updateProduct(1L, updatedDto);

            assertThat(result).isNotNull();

            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).existsBySku("TEC-NEW-002");
            verify(productMapper, times(1)).updateEntityFromDto(updatedDto, product);
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Deve lançar DuplicateSkuException quando o novo SKU já pertencer a outro produto")
        void shouldThrowDuplicateSkuExceptionWhenNewSkuAlreadyExists() {
            ProductRequestDTO updatedDto = new ProductRequestDTO(
                    "Teclado Mecânico",
                    "TEC-EXISTENTE",
                    "Teclado RGB",
                    true,
                    new BigDecimal("350.00"),
                    new BigDecimal("200.00")
            );

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.existsBySku("TEC-EXISTENTE")).thenReturn(true);

            assertThatThrownBy(() -> productService.updateProduct(1L, updatedDto))
                    .isInstanceOf(DuplicateSkuException.class)
                    .hasMessage("O novo SKU informado já pertence a outro produto: TEC-EXISTENTE");

            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).existsBySku("TEC-EXISTENTE");
            verify(productMapper, never()).updateEntityFromDto(any(), any());
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar produto inexistente")
        void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistingProduct() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(99L, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Produto não encontrado com id: 99");

            verify(productRepository, times(1)).findById(99L);
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método activateProduct")
    class ActivateProductTests {

        @Test
        @DisplayName("Deve ativar o produto alterando o campo 'active' para true")
        void shouldActivateProductSuccessfully() {
            product.setActive(false);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);

            productService.activateProduct(1L);

            assertThat(product.getActive()).isTrue();

            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar ativar produto inexistente")
        void shouldThrowResourceNotFoundExceptionWhenActivatingNonExistingProduct() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.activateProduct(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Produto não encontrado com id: 99");

            verify(productRepository, times(1)).findById(99L);
            verify(productRepository, never()).save(any());
        }
    }
}