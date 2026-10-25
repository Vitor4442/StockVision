package stockvision.finance.Financial;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stockvision.finance.Financial.dto.FinancialEntryRequest;
import stockvision.finance.Financial.dto.FinancialEntryResponse;
import stockvision.finance.Financial.exception.FinancialEntryNotFoundException;
import stockvision.finance.Financial.exception.FinancialEntryStateException;

@Service
public class FinancialService {
    private final FinanciaRepository repository;

    public FinancialService(FinanciaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FinancialEntryResponse create(FinancialType type, FinancialEntryRequest request) {
        FinancialEntity entity = new FinancialEntity();
        entity.setType(type);
        entity.setDescription(request.description().trim());
        entity.setAmount(request.amount());
        entity.setIssueDate(request.issueDate());
        entity.setDueDate(request.dueDate());
        entity.setCategoryId(request.categoryId());
        entity.setCounterpartyId(request.counterpartyId());
        entity.setSourceType(request.sourceType());
        entity.setSourceId(request.sourceId());
        entity.setStatus(FinancialStatus.PENDING);
        return FinancialEntryResponse.from(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<FinancialEntryResponse> findAll(FinancialType type, FinancialStatus status,
            LocalDate issueDateFrom, LocalDate issueDateTo, LocalDate dueDateFrom, LocalDate dueDateTo,
            UUID categoryId, UUID counterpartyId, boolean includeDeleted, Pageable pageable) {
        Specification<FinancialEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!includeDeleted) predicates.add(cb.isNull(root.get("deletedAt")));
            if (type != null) predicates.add(cb.equal(root.get("type"), type));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (issueDateFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("issueDate"), issueDateFrom));
            if (issueDateTo != null) predicates.add(cb.lessThanOrEqualTo(root.get("issueDate"), issueDateTo));
            if (dueDateFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
            if (dueDateTo != null) predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
            if (categoryId != null) predicates.add(cb.equal(root.get("categoryId"), categoryId));
            if (counterpartyId != null) predicates.add(cb.equal(root.get("counterpartyId"), counterpartyId));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(spec, pageable).map(FinancialEntryResponse::from);
    }

    @Transactional(readOnly = true)
    public FinancialEntryResponse findById(UUID id) {
        return FinancialEntryResponse.from(getEntry(id));
    }

    @Transactional
    public FinancialEntryResponse settle(UUID id) {
        FinancialEntity entity = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new FinancialEntryNotFoundException(id));
        ensureActive(entity);
        if (entity.getStatus() != FinancialStatus.PENDING) {
            throw new FinancialEntryStateException("Only pending financial entries can be settled.");
        }
        entity.setStatus(FinancialStatus.PAID);
        return FinancialEntryResponse.from(repository.save(entity));
    }

    @Transactional
    public FinancialEntryResponse cancel(UUID id) {
        FinancialEntity entity = getEntry(id);
        ensureActive(entity);
        if (entity.getStatus() != FinancialStatus.PENDING) {
            throw new FinancialEntryStateException("Only pending financial entries can be cancelled.");
        }
        entity.setStatus(FinancialStatus.CANCELLED);
        return FinancialEntryResponse.from(repository.save(entity));
    }

    @Transactional
    public FinancialEntryResponse softDelete(UUID id) {
        FinancialEntity entity = getEntry(id);
        ensureActive(entity);
        entity.setDeletedAt(LocalDateTime.now());
        return FinancialEntryResponse.from(repository.save(entity));
    }

    @Transactional
    public FinancialEntryResponse restore(UUID id) {
        FinancialEntity entity = getEntry(id);
        if (entity.getDeletedAt() == null) {
            throw new FinancialEntryStateException("Financial entry is not deleted.");
        }
        entity.setDeletedAt(null);
        return FinancialEntryResponse.from(repository.save(entity));
    }

    private FinancialEntity getEntry(UUID id) {
        return repository.findById(id).orElseThrow(() -> new FinancialEntryNotFoundException(id));
    }

    private void ensureActive(FinancialEntity entity) {
        if (entity.getDeletedAt() != null) {
            throw new FinancialEntryStateException("Deleted financial entries cannot be changed.");
        }
    }
}
