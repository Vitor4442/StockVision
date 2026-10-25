package stockvision.finance.Category.exception;

import java.util.UUID;

public class FinancialCategoryNotFoundException extends RuntimeException {
    public FinancialCategoryNotFoundException(UUID id) {
        super("Financial category " + id + " was not found.");
    }
}
