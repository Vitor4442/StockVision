package stockvision.procurement.purchase_order_items;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import stockvision.procurement.purchase_order_items.dto.PurchaseOrderItemsRequest;
import stockvision.procurement.purchase_order_items.dto.PurchaseOrderItemsResponse;
import stockvision.procurement.client.InventoryProductClient;
import stockvision.procurement.purchase_order_items.mapper.PurchaseOrderItemsMapper;
import stockvision.procurement.purchase_orders.PurchaseOrders;
import stockvision.procurement.purchase_orders.PurchaseOrdersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderItemsService {

    private final PurchaseOrderItemsRepository itemsRepository;
    private final PurchaseOrdersRepository purchaseOrdersRepository;
    private final InventoryProductClient inventoryProductClient;
    private final PurchaseOrderItemsMapper itemsMapper;

    @Transactional(readOnly = true)
    public List<PurchaseOrderItemsResponse> findAll(Long purchaseOrderId) {
        List<PurhaseOrderItems> items = purchaseOrderId == null ? itemsRepository.findAll() : itemsRepository.findByPurchaseOrdersId(purchaseOrderId);
        return items.stream().map(itemsMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PurchaseOrderItemsResponse findById(Long id) {
        return itemsMapper.toResponse(getItemById(id));
    }

    @Transactional
    public PurchaseOrderItemsResponse create(PurchaseOrderItemsRequest request) {
        validateProductExists(request.productId());
        PurchaseOrders order = getOrderById(request.purchaseOrderId());
        PurhaseOrderItems item = itemsMapper.toEntity(request, order);
        return itemsMapper.toResponse(itemsRepository.save(item));
    }

    @Transactional
    public PurchaseOrderItemsResponse update(Long id, PurchaseOrderItemsRequest request) {
        PurhaseOrderItems item = getItemById(id);
        validateProductExists(request.productId());
        item.setPurchaseOrders(getOrderById(request.purchaseOrderId()));
        item.setProductId(request.productId());
        item.setQuantity(request.quantity());
        return itemsMapper.toResponse(itemsRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        itemsRepository.delete(getItemById(id));
    }

    private void validateProductExists(Long productId) {
        try {
            inventoryProductClient.findProductById(productId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + productId);
        } catch (FeignException.Unauthorized exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Inventory não autorizou a consulta do produto", exception);
        } catch (FeignException.Forbidden exception) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Sem permissão para consultar o produto no inventory", exception);
        } catch (FeignException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível validar o produto no serviço de inventory", exception);
        }
    }

    private PurhaseOrderItems getItemById(Long id) {
        return itemsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item do pedido não encontrado: " + id));
    }

    private PurchaseOrders getOrderById(Long id) {
        return purchaseOrdersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado: " + id));
    }
}
