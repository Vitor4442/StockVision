package stockvision.inventory.product;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import stockvision.inventory.product.dto.ProductRequestDTO;
import stockvision.inventory.product.dto.ProductResponseDTO;
import stockvision.inventory.shared.exception.DuplicateSkuException;
import stockvision.inventory.shared.exception.ResourceNotFoundException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
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
    @DisplayName("GET /v1/products - Listagem paginada")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar 200 OK com página de produtos")
        void shouldReturnPagedProductsAndStatusOK() throws Exception {
            Page<ProductResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(productService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/v1/products")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].name").value("Teclado Mecânico"))
                    .andExpect(jsonPath("$.content[0].sku").value("TEC-001"))
                    .andExpect(jsonPath("$.content[0].price").value(350.00));

            verify(productService, times(1)).findAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /v1/products/{id} - Busca por ID")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar 200 OK quando o produto for encontrado")
        void shouldReturnProductAndStatusOKWhenFound() throws Exception {
            when(productService.findById(1L)).thenReturn(responseDTO);

            mockMvc.perform(get("/v1/products/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Teclado Mecânico"))
                    .andExpect(jsonPath("$.sku").value("TEC-001"));

            verify(productService, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found quando o produto não existir")
        void shouldReturnStatusNotFoundWhenProductDoesNotExist() throws Exception {
            when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Produto não encontrado com id: 99"));

            mockMvc.perform(get("/v1/products/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(productService, times(1)).findById(99L);
        }
    }

    @Nested
    @DisplayName("POST /v1/products - Cadastro de produto")
    class CreateTests {

        @Test
        @DisplayName("Deve retornar 201 Created com o produto criado quando os dados forem válidos")
        void shouldCreateProductAndReturnStatusCreated() throws Exception {
            when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Teclado Mecânico"))
                    .andExpect(jsonPath("$.sku").value("TEC-001"));

            verify(productService, times(1)).createProduct(any(ProductRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request ao enviar JSON com dados inválidos (@Valid)")
        void shouldReturnStatusBadRequestWhenDtoIsInvalid() throws Exception {
            // DTO sem nome e sem preço (assumindo que possuem validações @NotNull / @NotBlank)
            ProductRequestDTO invalidDto = new ProductRequestDTO(
                    "",
                    "SKU-123",
                    "Descrição",
                    true,
                    null,
                    new BigDecimal("10.00")
            );

            mockMvc.perform(post("/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(productService);
        }
    }

    @Nested
    @DisplayName("PUT /v1/products/{id} - Atualização de produto")
    class UpdateTests {

        @Test
        @DisplayName("Deve retornar 200 OK com o produto atualizado")
        void shouldUpdateProductAndReturnStatusOK() throws Exception {
            when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/v1/products/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.sku").value("TEC-001"));

            verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 409 Conflict ou 400 Bad Request se houver SKU duplicado")
        void shouldReturnErrorStatusWhenSkuIsDuplicate() throws Exception {
            when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class)))
                    .thenThrow(new DuplicateSkuException("O novo SKU informado já pertence a outro produto"));

            mockMvc.perform(put("/v1/products/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    // O status exato depende do tratamento no seu @ControllerAdvice / GlobalExceptionHandler
                    .andExpect(status().is4xxClientError());

            verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("PATCH /v1/products/{id}/activate - Ativação de produto")
    class ActivateTests {

        @Test
        @DisplayName("Deve retornar 204 No Content ao ativar produto com sucesso")
        void shouldActivateProductAndReturnNoContent() throws Exception {
            doNothing().when(productService).activateProduct(1L);

            mockMvc.perform(patch("/v1/products/{id}/activate", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(productService, times(1)).activateProduct(1L);
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found ao tentar ativar produto inexistente")
        void shouldReturnStatusNotFoundWhenActivatingNonExistingProduct() throws Exception {
            doThrow(new ResourceNotFoundException("Produto não encontrado com id: 99"))
                    .when(productService).activateProduct(99L);

            mockMvc.perform(patch("/v1/products/{id}/activate", 99L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(productService, times(1)).activateProduct(99L);
        }
    }
}