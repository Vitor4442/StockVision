package stockvision.inventory.movement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockvision.inventory.movement.dto.StockMovementRequestDTO;
import stockvision.inventory.movement.dto.StockMovementTrackingCodeRequestDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stocksMovement")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping("/removeStock")
    public ResponseEntity<Void> removeStock (@RequestBody @Valid StockMovementRequestDTO stockMovementRequestDTO){
        stockMovementService.RemoveQuantity(stockMovementRequestDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/addStock")
    public ResponseEntity<Void> addStock (@RequestBody @Valid StockMovementRequestDTO stockMovementRequestDTO){
        stockMovementService.AddQuantity(stockMovementRequestDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/tracking/{trackingCode}/remove")
    public ResponseEntity<Void> removeStockByTrackingCode(@PathVariable String trackingCode, @RequestBody @Valid StockMovementTrackingCodeRequestDTO request) {
        stockMovementService.removeQuantityByTrackingCode(trackingCode, request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/tracking/{trackingCode}/add")
    public ResponseEntity<Void> addStockByTrackingCode(@PathVariable String trackingCode, @RequestBody @Valid StockMovementTrackingCodeRequestDTO request) {
        stockMovementService.addQuantityByTrackingCode(trackingCode, request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/adjusted")
    public ResponseEntity<Void> adjustedStock (@RequestBody @Valid StockMovementRequestDTO stockMovementRequestDTO){
        stockMovementService.AdjustedQuantity(stockMovementRequestDTO);
        return ResponseEntity.accepted().build();
    }
}
