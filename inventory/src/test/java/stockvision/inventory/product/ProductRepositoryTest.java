package stockvision.inventory.product;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    private Product validProduct;

    @BeforeEach
    void setUp() {
        validProduct = new Product();
        validProduct.setName("Teclado Mecânico");
        validProduct.setSku("TEC-MEC-001");
        validProduct.setDescription("Teclado RGB Switch Blue");
        validProduct.setPrice(new BigDecimal("350.00"));
        validProduct.setCostPrice(new BigDecimal("200.00"));
        validProduct.setActive(true);
    }

    @Test
    @DisplayName("Deve persistir produto com sucesso quando todos os dados forem válidos")
    void shouldSaveProductSuccessfully() {
        Product savedProduct = entityManager.persistAndFlush(validProduct);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Teclado Mecânico");
        assertThat(savedProduct.getSku()).isEqualTo("TEC-MEC-001");
        assertThat(savedProduct.getActive()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar produto com SKU duplicado")
    void shouldFailWhenSkuIsDuplicate() {
        entityManager.persistAndFlush(validProduct);

        Product duplicateProduct = new Product();
        duplicateProduct.setName("Teclado ABNT2");
        duplicateProduct.setSku("TEC-MEC-001"); // SKU duplicado
        duplicateProduct.setPrice(new BigDecimal("300.00"));
        duplicateProduct.setCostPrice(new BigDecimal("180.00"));

        assertThatThrownBy(() -> entityManager.persistAndFlush(duplicateProduct))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar produto com preço nulo")
    void shouldFailWhenPriceIsNull() {
        validProduct.setPrice(null);

        assertThatThrownBy(() -> entityManager.persistAndFlush(validProduct))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("price");
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar produto com preço negativo")
    void shouldFailWhenPriceIsNegative() {
        validProduct.setPrice(new BigDecimal("-10.00"));

        assertThatThrownBy(() -> entityManager.persistAndFlush(validProduct))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("price");
    }

    @Test
    @DisplayName("Deve permitir salvar produto com preço igual a zero")
    void shouldAllowZeroPrice() {
        validProduct.setPrice(BigDecimal.ZERO);
        validProduct.setCostPrice(BigDecimal.ZERO);

        Product savedProduct = entityManager.persistAndFlush(validProduct);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar produto com preço de custo negativo")
    void shouldFailWhenCostPriceIsNegative() {
        validProduct.setCostPrice(new BigDecimal("-5.00"));

        assertThatThrownBy(() -> entityManager.persistAndFlush(validProduct))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("costPrice");
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar produto com nome nulo")
    void shouldFailWhenNameIsNull() {
        validProduct.setName(null);

        assertThatThrownBy(() -> entityManager.persistAndFlush(validProduct))
                .isInstanceOf(PersistenceException.class);
    }
}