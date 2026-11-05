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

import java.math.BigDecimal;
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
        BigDecimal unitValue = getProductPrice(request.productId());
        PurchaseOrders order = getOrderById(request.purchaseOrderId());
        PurhaseOrderItems item = itemsMapper.toEntity(request, order);
        item.setUnitValue(unitValue);
        item.setTotalValue(request.quantity().multiply(unitValue));
        PurhaseOrderItems savedItem = itemsRepository.save(item);
        recalculateOrderTotals(order);
        return itemsMapper.toResponse(savedItem);
    }

    @Transactional
    public PurchaseOrderItemsResponse update(Long id, PurchaseOrderItemsRequest request) {
        PurhaseOrderItems item = getItemById(id);
        BigDecimal unitValue = getProductPrice(request.productId());
        PurchaseOrders previousOrder = item.getPurchaseOrders();
        PurchaseOrders newOrder = getOrderById(request.purchaseOrderId());
        item.setPurchaseOrders(newOrder);
        item.setProductId(request.productId());
        item.setQuantity(request.quantity());
        item.setUnitValue(unitValue);
        item.setTotalValue(request.quantity().multiply(unitValue));
        PurhaseOrderItems savedItem = itemsRepository.save(item);
        if (previousOrder != null && !previousOrder.getId().equals(newOrder.getId())) {
            recalculateOrderTotals(previousOrder);
        }
        recalculateOrderTotals(newOrder);
        return itemsMapper.toResponse(savedItem);
    }

    @Transactional
    public void delete(Long id) {
        PurhaseOrderItems item = getItemById(id);
        PurchaseOrders order = item.getPurchaseOrders();
        itemsRepository.delete(item);
        if (order != null) {
            recalculateOrderTotals(order);
        }
    }

    private void recalculateOrderTotals(PurchaseOrders order) {
        BigDecimal subtotal = itemsRepository.findByPurchaseOrdersId(order.getId()).stream()
                .map(PurhaseOrderItems::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotal(subtotal);
        order.calculateTotalAmount();
        purchaseOrdersRepository.save(order);
    }

    private BigDecimal getProductPrice(Long productId) {
        try {
            return inventoryProductClient.findProductById(productId).price();
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
