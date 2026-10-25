package stockvision.finance.Category;

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
import stockvision.finance.Category.dto.FinancialCategoryRequest;
import stockvision.finance.Category.dto.FinancialCategoryResponse;
import stockvision.finance.Financial.FinancialType;

@RestController
@RequestMapping("/api/financial-categories")
public class FinancialCategoryController {
    private final FinancialCategoryService service;

    public FinancialCategoryController(FinancialCategoryService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<FinancialCategoryResponse> create(@Valid @RequestBody FinancialCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public Page<FinancialCategoryResponse> findAll(
            @RequestParam(required = false) FinancialType type,
            @RequestParam(required = false) FinancialCategoryStatus status,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.findAll(type, status, includeDeleted, pageable);
    }

    @GetMapping("/{id}")
    public FinancialCategoryResponse findById(@PathVariable UUID id) { return service.findById(id); }

    @PutMapping("/{id}")
    public FinancialCategoryResponse update(@PathVariable UUID id,
            @Valid @RequestBody FinancialCategoryRequest request) { return service.update(id, request); }

    @PatchMapping("/{id}/status")
    public FinancialCategoryResponse changeStatus(@PathVariable UUID id,
            @RequestParam FinancialCategoryStatus status) { return service.changeStatus(id, status); }

    @PatchMapping("/{id}/restore")
    public FinancialCategoryResponse restore(@PathVariable UUID id) { return service.restore(id); }

    @PatchMapping("/{id}/delete")
    public FinancialCategoryResponse softDelete(@PathVariable UUID id) { return service.softDelete(id); }
}
