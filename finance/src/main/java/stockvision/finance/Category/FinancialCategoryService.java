package stockvision.finance.Category;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stockvision.finance.Category.dto.FinancialCategoryRequest;
import stockvision.finance.Category.dto.FinancialCategoryResponse;
import stockvision.finance.Category.exception.FinancialCategoryConflictException;
import stockvision.finance.Category.exception.FinancialCategoryNotFoundException;
import stockvision.finance.Financial.FinanciaRepository;

@Service
public class FinancialCategoryService {
    private final FinancialCategoryRepository repository;
    private final FinanciaRepository entryRepository;

    public FinancialCategoryService(FinancialCategoryRepository repository, FinanciaRepository entryRepository) {
        this.repository = repository;
        this.entryRepository = entryRepository;
    }

    @Transactional
    public FinancialCategoryResponse create(FinancialCategoryRequest request) {
        ensureNameAvailable(request.name(), request.type(), null);
        FinancialCategory category = new FinancialCategory();
        apply(category, request);
        category.setStatus(FinancialCategoryStatus.ACTIVE);
        return FinancialCategoryResponse.from(repository.save(category));
    }

    @Transactional(readOnly = true)
    public Page<FinancialCategoryResponse> findAll(stockvision.finance.Financial.FinancialType type,
            FinancialCategoryStatus status, boolean includeDeleted, Pageable pageable) {
        Specification<FinancialCategory> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!includeDeleted) predicates.add(cb.isNull(root.get("deletedAt")));
            if (type != null) predicates.add(cb.equal(root.get("type"), type));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(spec, pageable).map(FinancialCategoryResponse::from);
    }

    @Transactional(readOnly = true)
    public FinancialCategoryResponse findById(UUID id) {
        return FinancialCategoryResponse.from(getCategory(id));
    }

    @Transactional
    public FinancialCategoryResponse update(UUID id, FinancialCategoryRequest request) {
        FinancialCategory category = getCategory(id);
        ensureNotDeleted(category);
        ensureNameAvailable(request.name(), request.type(), id);
        if (category.getType() != request.type() && entryRepository.existsByCategoryId(id)) {
            throw new FinancialCategoryConflictException(
                    "Category type cannot change after it has been used by financial entries.");
        }
        apply(category, request);
        return FinancialCategoryResponse.from(repository.save(category));
    }

    @Transactional
    public FinancialCategoryResponse changeStatus(UUID id, FinancialCategoryStatus status) {
        FinancialCategory category = getCategory(id);
        ensureNotDeleted(category);
        category.setStatus(status);
        return FinancialCategoryResponse.from(repository.save(category));
    }

    @Transactional
    public FinancialCategoryResponse softDelete(UUID id) {
        FinancialCategory category = getCategory(id);
        ensureNotDeleted(category);
        category.setDeletedAt(LocalDateTime.now());
        return FinancialCategoryResponse.from(repository.save(category));
    }

    @Transactional
    public FinancialCategoryResponse restore(UUID id) {
        FinancialCategory category = getCategory(id);
        if (category.getDeletedAt() == null) {
            throw new FinancialCategoryConflictException("Financial category is not deleted.");
        }
        ensureNameAvailable(category.getName(), category.getType(), id);
        category.setDeletedAt(null);
        return FinancialCategoryResponse.from(repository.save(category));
    }

    private void apply(FinancialCategory category, FinancialCategoryRequest request) {
        category.setName(request.name().trim());
        category.setDescription(request.description() == null || request.description().isBlank()
                ? null : request.description().trim());
        category.setType(request.type());
    }

    private void ensureNameAvailable(String name, stockvision.finance.Financial.FinancialType type, UUID excludedId) {
        String normalizedName = name.trim();
        boolean exists = excludedId == null
                ? repository.existsByNameIgnoreCaseAndTypeAndDeletedAtIsNull(normalizedName, type)
                : repository.existsByNameIgnoreCaseAndTypeAndDeletedAtIsNullAndIdNot(normalizedName, type, excludedId);
        if (exists) {
            throw new FinancialCategoryConflictException("A category with this name and type already exists.");
        }
    }

    private FinancialCategory getCategory(UUID id) {
        return repository.findById(id).orElseThrow(() -> new FinancialCategoryNotFoundException(id));
    }

    private void ensureNotDeleted(FinancialCategory category) {
        if (category.getDeletedAt() != null) {
            throw new FinancialCategoryConflictException("Deleted financial categories cannot be changed.");
        }
    }
}
