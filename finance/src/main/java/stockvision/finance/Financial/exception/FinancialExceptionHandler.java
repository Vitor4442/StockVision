package stockvision.finance.Financial.exception;

import stockvision.finance.Account.exception.FinancialAccountNotFoundException;
import stockvision.finance.Settlement.exception.FinancialSettlementNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FinancialExceptionHandler {
    @ExceptionHandler(FinancialSettlementNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSettlementNotFound(FinancialSettlementNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(FinancialAccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(FinancialAccountNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(FinancialEntryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(FinancialEntryNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(FinancialEntryStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidState(FinancialEntryStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error(HttpStatus.CONFLICT, exception.getMessage()));
    }

    private Map<String, Object> error(HttpStatus status, String message) {
        return Map.of("timestamp", LocalDateTime.now(), "status", status.value(),
                "error", status.getReasonPhrase(), "message", message);
    }
}
