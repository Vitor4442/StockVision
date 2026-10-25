package stockvision.finance.Settlement;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stockvision.finance.Settlement.dto.FinancialSettlementRequest;
import stockvision.finance.Settlement.dto.FinancialSettlementResponse;

@RestController
@RequestMapping("/api/financial-settlements")
public class FinancialSettlementController {
    private final FinancialSettlementService service;

    public FinancialSettlementController(FinancialSettlementService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<FinancialSettlementResponse> create(
            @Valid @RequestBody FinancialSettlementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<FinancialSettlementResponse> findAll(
            @RequestParam(required = false) UUID financialEntryId) {
        return service.findAll(financialEntryId);
    }

    @GetMapping("/{id}")
    public FinancialSettlementResponse findById(@PathVariable UUID id) { return service.findById(id); }

    @PatchMapping("/{id}/cancel")
    public FinancialSettlementResponse cancel(@PathVariable UUID id) { return service.cancel(id); }
}
