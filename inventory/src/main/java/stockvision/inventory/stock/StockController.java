package stockvision.inventory.stock;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockvision.inventory.stock.dto.StockRequestDTO;
import stockvision.inventory.stock.dto.StockResponseDTO;
import stockvision.inventory.stock.dto.UpdateStockQuantityDTO;

import java.util.List;

@RestController
@RequestMapping("/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/products/{productId}/locations/{locationId}")
    public ResponseEntity<StockResponseDTO> getStock(@PathVariable Long productId, @PathVariable Long locationId) {
        return ResponseEntity.ok(stockService.getByProductAndLocation(productId, locationId));
    }

    @GetMapping("/locations/{locationId}")
    public ResponseEntity<List<StockResponseDTO>> getStockFromLocation(@PathVariable Long locationId){
        return ResponseEntity.ok(stockService.getByLocation(locationId));
    }

    @PostMapping
    public ResponseEntity<StockResponseDTO> create(@RequestBody @Valid StockRequestDTO dto) {
        StockResponseDTO created = stockService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/products/{productId}/locations/{locationId}/add")
    public ResponseEntity<StockResponseDTO> addQuantity(@PathVariable Long productId, @PathVariable Long locationId, @RequestBody @Valid UpdateStockQuantityDTO dto) {
        return ResponseEntity.ok(stockService.addQuantity(productId, locationId, dto));
    }

    @PatchMapping("/products/{productId}/locations/{locationId}/remove")
    public ResponseEntity<StockResponseDTO> removeQuantity(@PathVariable Long productId, @PathVariable Long locationId, @RequestBody @Valid UpdateStockQuantityDTO dto) {
        return ResponseEntity.ok(stockService.removeQuantity(productId, locationId, dto));
    }
}