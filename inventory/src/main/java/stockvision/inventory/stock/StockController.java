package stockvision.inventory.stock;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public ResponseEntity<Page<StockResponseDTO>> getStockAll( @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(stockService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<StockResponseDTO> create(@RequestBody @Valid StockRequestDTO dto) {
        StockResponseDTO created = stockService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}