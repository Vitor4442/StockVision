package stockvision.finance.Settlement.dto;

import java.math.BigDecimal;
import java.util.UUID;
import stockvision.finance.Financial.FinancialType;

public record FinancialSettlementAccountTotal(UUID financialAccountId, FinancialType type, BigDecimal amount) {}
