package stockvision.finance.Account.exception;

import java.util.UUID;

public class FinancialAccountNotFoundException extends RuntimeException {
    public FinancialAccountNotFoundException(UUID id) {
        super("Financial account " + id + " was not found.");
    }
}
