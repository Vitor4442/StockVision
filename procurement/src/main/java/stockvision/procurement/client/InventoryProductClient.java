package stockvision.procurement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import stockvision.procurement.client.dto.InventoryProductResponse;

@FeignClient(name = "inventoryProductClient", url = "${app.inventory.base-url}")
public interface InventoryProductClient {

    @GetMapping("/v1/products/{id}")
    InventoryProductResponse findProductById(@PathVariable("id") Long id);
}
