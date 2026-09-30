package stockvision.inventory.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {
    @Test
    @DisplayName("Deve criar produto usando construtor sem argumentos e getters/setters")
    void shouldCreateProductWithNoArgsConstructorAndSetters() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Notebook Dell");
        product.setSku("NB-DELL-001");
        product.setDescription("Notebook i7 16GB");
        product.setActive(true);
        product.setPrice(new BigDecimal("4500.00"));
        product.setCostPrice(new BigDecimal("3500.00"));

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Notebook Dell");
        assertThat(product.getSku()).isEqualTo("NB-DELL-001");
        assertThat(product.getDescription()).isEqualTo("Notebook i7 16GB");
        assertThat(product.getActive()).isTrue();
        assertThat(product.getPrice()).isEqualByComparingTo("4500.00");
        assertThat(product.getCostPrice()).isEqualByComparingTo("3500.00");
    }

    @Test
    @DisplayName("Deve criar produto usando construtor com todos os argumentos")
    void shouldCreateProductWithAllArgsConstructor() {
        Product product = new Product(
                2L,
                "Mouse Sem Fio",
                "MS-LOGI-002",
                "Mouse ergonômico",
                false,
                new BigDecimal("150.00"),
                new BigDecimal("80.00")
        );

        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getName()).isEqualTo("Mouse Sem Fio");
        assertThat(product.getSku()).isEqualTo("MS-LOGI-002");
        assertThat(product.getDescription()).isEqualTo("Mouse ergonômico");
        assertThat(product.getActive()).isFalse();
        assertThat(product.getPrice()).isEqualByComparingTo("150.00");
        assertThat(product.getCostPrice()).isEqualByComparingTo("80.00");
    }

    @Test
    @DisplayName("Deve garantir que o valor padrão de 'active' seja true ao instanciar")
    void shouldHaveDefaultActiveAsTrue() {
        Product product = new Product();
        assertThat(product.getActive()).isTrue();
    }
}
