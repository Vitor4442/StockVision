package stockvision.inventory.product;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import stockvision.inventory.product.dto.ProductRequestDTO;
import stockvision.inventory.product.dto.ProductResponseDTO;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequestDTO dto);

    ProductResponseDTO toResponseDTO(Product entity);

    void updateEntityFromDto(ProductRequestDTO dto, @MappingTarget Product entity);
}