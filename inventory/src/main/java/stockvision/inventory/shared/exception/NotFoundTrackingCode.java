package stockvision.inventory.shared.exception;

public class NotFoundTrackingCode extends RuntimeException {
    public NotFoundTrackingCode(String message) {
        super(message);
    }
}
