package stockvision.finance.Settlement.exception;

import java.util.UUID;

public class FinancialSettlementNotFoundException extends RuntimeException {
    public FinancialSettlementNotFoundException(UUID id) {
        super("Financial settlement " + id + " was not found.");
    }
}
