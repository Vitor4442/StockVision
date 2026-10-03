package stockvision.inventory.location;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockvision.inventory.location.dto.LocationRequest;
import stockvision.inventory.location.dto.LocationResponse;

@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<Page<LocationResponse>> getAllLocations(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(locationService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@RequestBody @Valid LocationRequest request) {
        LocationResponse createdLocation = locationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(@PathVariable Long id, @RequestBody @Valid LocationRequest request) {
        return ResponseEntity.ok(locationService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<LocationResponse> toggleActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.toggleActiveStatus(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}