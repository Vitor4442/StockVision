package stockvision.finance.Settlement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "financial_settlements")
@Check(constraints = "amount > 0")
public class FinancialSettlement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "financial_entry_id", nullable = false)
    private UUID financialEntryId;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @NotNull
    @Column(name = "financial_account_id", nullable = false)
    private UUID financialAccountId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FinancialSettlementMethod method;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FinancialSettlementStatus status;

    @PrePersist
    protected void onCreate() {
        if (paidAt == null) paidAt = LocalDateTime.now();
        if (status == null) status = FinancialSettlementStatus.CONFIRMED;
    }

    public UUID getId() { return id; }
    public UUID getFinancialEntryId() { return financialEntryId; }
    public void setFinancialEntryId(UUID financialEntryId) { this.financialEntryId = financialEntryId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public UUID getFinancialAccountId() { return financialAccountId; }
    public void setFinancialAccountId(UUID financialAccountId) { this.financialAccountId = financialAccountId; }
    public FinancialSettlementMethod getMethod() { return method; }
    public void setMethod(FinancialSettlementMethod method) { this.method = method; }
    public FinancialSettlementStatus getStatus() { return status; }
    public void setStatus(FinancialSettlementStatus status) { this.status = status; }
}
