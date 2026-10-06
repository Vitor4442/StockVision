package stockvision.inventory.location;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import stockvision.inventory.location.dto.LocationRequest;
import stockvision.inventory.location.dto.LocationResponse;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toEntity(LocationRequest request);
    LocationResponse toResponse(Location entity);
    void updateEntityFromDto (LocationRequest dto, @MappingTarget Location entity);
}
