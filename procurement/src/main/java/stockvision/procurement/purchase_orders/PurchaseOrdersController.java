package stockvision.procurement.purchase_orders;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockvision.procurement.purchase_orders.dto.PurchaseOrdersRequest;
import stockvision.procurement.purchase_orders.dto.PurchaseOrdersResponse;

import java.util.List;

@RestController
@RequestMapping("/v1/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrdersController {

    private final PurchaseOrdersService purchaseOrdersService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrdersResponse>> findAll() {
        return ResponseEntity.ok(purchaseOrdersService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrdersResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrdersService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseOrdersResponse> create(@RequestBody @Valid PurchaseOrdersRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrdersService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrdersResponse> update(@PathVariable Long id,
                                                          @RequestBody @Valid PurchaseOrdersRequest request) {
        return ResponseEntity.ok(purchaseOrdersService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseOrdersService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
