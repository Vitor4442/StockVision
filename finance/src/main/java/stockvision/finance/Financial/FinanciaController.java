package stockvision.finance.Financial;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import stockvision.finance.Financial.dto.FinancialEntryRequest;
import stockvision.finance.Financial.dto.FinancialEntryResponse;

@RestController
@RequestMapping("/api/financial-entries")
public class FinanciaController {
    private final FinancialService service;

    public FinanciaController(FinancialService service) {
        this.service = service;
    }

    @PostMapping("/payables")
    public ResponseEntity<FinancialEntryResponse> createPayable(@Valid @RequestBody FinancialEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(FinancialType.PAYABLE, request));
    }

    @PostMapping("/receivables")
    public ResponseEntity<FinancialEntryResponse> createReceivable(@Valid @RequestBody FinancialEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(FinancialType.RECEIVABLE, request));
    }

    @GetMapping
    public Page<FinancialEntryResponse> findAll(
            @RequestParam(required = false) FinancialType type,
            @RequestParam(required = false) FinancialStatus status,
            @RequestParam(required = false) LocalDate issueDateFrom,
            @RequestParam(required = false) LocalDate issueDateTo,
            @RequestParam(required = false) LocalDate dueDateFrom,
            @RequestParam(required = false) LocalDate dueDateTo,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID counterpartyId,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return service.findAll(type, status, issueDateFrom, issueDateTo, dueDateFrom, dueDateTo,
                categoryId, counterpartyId, includeDeleted, pageable);
    }

    @GetMapping("/{id}")
    public FinancialEntryResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping("/{id}/settlements")
    public FinancialEntryResponse settle(@PathVariable UUID id) {
        return service.settle(id);
    }

    @PatchMapping("/{id}/cancel")
    public FinancialEntryResponse cancel(@PathVariable UUID id) {
        return service.cancel(id);
    }

    @PatchMapping("/{id}/restore")
    public FinancialEntryResponse restore(@PathVariable UUID id) {
        return service.restore(id);
    }

    @PatchMapping("/{id}/delete")
    public FinancialEntryResponse softDelete(@PathVariable UUID id) {
        return service.softDelete(id);
    }
}
