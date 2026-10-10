package stockvision.inventory.shared.exception;

public class DuplicateStockProductAndLocation extends RuntimeException {
    public DuplicateStockProductAndLocation(String message) {
        super(message);
    }
}
