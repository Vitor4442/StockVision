package stockvision.procurement.purchase_orders;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import stockvision.procurement.purchase_orders.dto.PurchaseOrdersRequest;
import stockvision.procurement.purchase_orders.dto.PurchaseOrdersResponse;
import stockvision.procurement.purchase_orders.mapper.PurchaseOrdersMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrdersService {

    private final PurchaseOrdersRepository purchaseOrdersRepository;
    private final PurchaseOrdersMapper purchaseOrdersMapper;

    @Transactional(readOnly = true)
    public List<PurchaseOrdersResponse> findAll() {
        return purchaseOrdersRepository.findAll().stream().map(purchaseOrdersMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PurchaseOrdersResponse findById(Long id) {
        return purchaseOrdersMapper.toResponse(getEntityById(id));
    }

    @Transactional
    public PurchaseOrdersResponse create(PurchaseOrdersRequest request) {
        PurchaseOrders order = purchaseOrdersMapper.toEntity(request);
        order.calculateTotalAmount();
        order.setCreatedAt(LocalDateTime.now());
        return purchaseOrdersMapper.toResponse(purchaseOrdersRepository.save(order));
    }

    @Transactional
    public PurchaseOrdersResponse update(Long id, PurchaseOrdersRequest request) {
        PurchaseOrders order = getEntityById(id);
        PurchaseOrders updated = purchaseOrdersMapper.toEntity(request);
        order.setOrderNumber(updated.getOrderNumber());
        order.setStatus(updated.getStatus());
        order.setDiscount(updated.getDiscount());
        order.setShippingCost(updated.getShippingCost());
        order.setNotes(updated.getNotes());
        order.setExpectedDeliveryAt(updated.getExpectedDeliveryAt());
        order.setOrderedAt(updated.getOrderedAt());
        order.setUpdatedAt(LocalDateTime.now());
        order.calculateTotalAmount();
        return purchaseOrdersMapper.toResponse(purchaseOrdersRepository.save(order));
    }

    @Transactional
    public void delete(Long id) {
        purchaseOrdersRepository.delete(getEntityById(id));
    }

    private PurchaseOrders getEntityById(Long id) {
        return purchaseOrdersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado: " + id));
    }
}
