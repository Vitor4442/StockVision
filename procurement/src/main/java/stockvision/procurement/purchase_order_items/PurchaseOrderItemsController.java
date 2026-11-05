package stockvision.procurement.purchase_order_items;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockvision.procurement.purchase_order_items.dto.PurchaseOrderItemsRequest;
import stockvision.procurement.purchase_order_items.dto.PurchaseOrderItemsResponse;

import java.util.List;

@RestController
@RequestMapping("/v1/purchase-order-items")
@RequiredArgsConstructor
public class PurchaseOrderItemsController {

    private final PurchaseOrderItemsService itemsService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrderItemsResponse>> findAll(
            @RequestParam(required = false) Long purchaseOrderId) {
        return ResponseEntity.ok(itemsService.findAll(purchaseOrderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderItemsResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(itemsService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderItemsResponse> create(@RequestBody @Valid PurchaseOrderItemsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemsService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderItemsResponse> update(@PathVariable Long id,
                                                              @RequestBody @Valid PurchaseOrderItemsRequest request) {
        return ResponseEntity.ok(itemsService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
