package stockvision.inventory.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public record ProductRequestDTO(
        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name,

        @NotBlank(message = "O SKU é obrigatório")
        @Size(max = 50, message = "O SKU deve ter no máximo 50 caracteres")
        String sku,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String description,

        @NotNull(message = "O status ativo/inativo é obrigatório")
        Boolean active,

        @NotNull(message = "O preço de venda é obrigatório")
        @PositiveOrZero(message = "O preço de venda não pode ser negativo")
        BigDecimal price,

        @NotNull(message = "O preço de compra é obrigatório")
        @PositiveOrZero(message = "O preço de compra não pode ser negativo")
        BigDecimal costPrice
) {}