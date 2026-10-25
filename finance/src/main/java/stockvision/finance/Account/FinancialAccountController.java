package stockvision.finance.Account;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stockvision.finance.Account.dto.FinancialAccountRequest;
import stockvision.finance.Account.dto.FinancialAccountResponse;

@RestController
@RequestMapping("/api/financial-accounts")
public class FinancialAccountController {
    private final FinancialAccountService service;

    public FinancialAccountController(FinancialAccountService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<FinancialAccountResponse> create(@Valid @RequestBody FinancialAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public Page<FinancialAccountResponse> findAll(
            @RequestParam(required = false) FinancialAccountType type,
            @RequestParam(required = false) FinancialAccountStatus status,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.findAll(type, status, includeDeleted, pageable);
    }

    @GetMapping("/{id}")
    public FinancialAccountResponse findById(@PathVariable UUID id) { return service.findById(id); }

    @PutMapping("/{id}")
    public FinancialAccountResponse update(@PathVariable UUID id, @Valid @RequestBody FinancialAccountRequest request) { return service.update(id, request); }

    @PatchMapping("/{id}/status")
    public FinancialAccountResponse changeStatus(@PathVariable UUID id, @RequestParam FinancialAccountStatus status) { return service.changeStatus(id, status); }

    @PatchMapping("/{id}/restore")
    public FinancialAccountResponse restore(@PathVariable UUID id) { return service.restore(id); }

    @PatchMapping("/{id}/delete")
    public FinancialAccountResponse softDelete(@PathVariable UUID id) { return service.softDelete(id); }
}
