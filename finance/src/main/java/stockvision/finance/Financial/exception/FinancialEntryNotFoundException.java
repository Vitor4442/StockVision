package stockvision.finance.Financial.exception;

import java.util.UUID;

public class FinancialEntryNotFoundException extends RuntimeException {
    public FinancialEntryNotFoundException(UUID id) {
        super("Financial entry not found: " + id);
    }
}
