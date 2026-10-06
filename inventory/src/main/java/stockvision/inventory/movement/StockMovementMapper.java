package stockvision.inventory.movement;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stockvision.inventory.movement.dto.StockMovementRequestDTO;
import stockvision.inventory.movement.dto.StockMovementResponseDTO;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    StockMovement toEntity(StockMovementRequestDTO dto);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "locationId", source = "location.id")
    StockMovementResponseDTO toResponseDTO(StockMovement entity);
}
