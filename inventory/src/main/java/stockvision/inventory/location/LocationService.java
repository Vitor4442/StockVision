package stockvision.inventory.location;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import stockvision.inventory.location.dto.LocationRequest;
import stockvision.inventory.location.dto.LocationResponse;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Page<LocationResponse> findAll(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public LocationResponse findById(Long id) {
        Location location = findEntityById(id);
        return toResponse(location);
    }

    @Transactional
    public LocationResponse create(LocationRequest request) {
        if (locationRepository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um local com o nome: " + request.name());
        }

        Location location = Location.builder()
                .name(request.name())
                .description(request.description())
                .active(true)
                .build();

        Location savedLocation = locationRepository.save(location);
        return toResponse(savedLocation);
    }

    @Transactional
    public LocationResponse update(Long id, LocationRequest request) {
        Location location = findEntityById(id);

        if (locationRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro local com o nome: " + request.name());
        }

        location.setName(request.name());
        location.setDescription(request.description());

        Location updatedLocation = locationRepository.save(location);
        return toResponse(updatedLocation);
    }

    @Transactional
    public LocationResponse toggleActiveStatus(Long id) {
        Location location = findEntityById(id);
        location.setActive(!location.getActive());
        Location updatedLocation = locationRepository.save(location);
        return toResponse(updatedLocation);
    }

    @Transactional
    public void delete(Long id) {
        Location location = findEntityById(id);
        locationRepository.delete(location);
    }

    private Location findEntityById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado com o ID: " + id));
    }

    private LocationResponse toResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getDescription(),
                Boolean.TRUE.equals(location.getActive())
        );
    }
}