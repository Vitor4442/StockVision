package stockvision.inventory.stock;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import stockvision.inventory.stock.dto.StockRequestDTO;
import stockvision.inventory.stock.dto.StockResponseDTO;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "locationId", source = "location.id")
    StockResponseDTO toDto(Stock stock);
    Stock toEntity(StockRequestDTO dto);
}
