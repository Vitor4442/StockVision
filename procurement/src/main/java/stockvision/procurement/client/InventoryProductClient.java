package stockvision.procurement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventoryProductClient", url = "${app.inventory.base-url}")
public interface InventoryProductClient {

    @GetMapping("/v1/products/{id}")
    void findProductById(@PathVariable("id") Long id);
}
