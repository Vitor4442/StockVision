package stockvision.inventory.stock;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import stockvision.inventory.location.Location;
import stockvision.inventory.product.Product;

import java.math.BigDecimal;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal quantity;

    @Version
    private Long version;

    public void removeStock(@NotNull(message = "A quantidade é obrigatória") @Positive(message = "A quantidade deve ser maior que zero") BigDecimal quantityRemove) {
        this.quantity = this.quantity.subtract(quantityRemove);
    }

    public void addStock(@NotNull(message = "A quantidade é obrigatória") @Positive(message = "A quantidade deve ser maior que zero") BigDecimal quantityAdd) {
        this.quantity = this.quantity.add(quantityAdd);
    }

    public void adjustedStock(@NotNull(message = "A quantidade é obrigatória") @Positive(message = "A quantidade deve ser maior que zero") BigDecimal quantity) {
        this.quantity = quantity;
    }
}