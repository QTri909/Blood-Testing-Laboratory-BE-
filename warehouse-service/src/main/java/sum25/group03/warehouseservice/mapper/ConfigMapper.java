package sum25.group03.warehouseservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sum25.group03.warehouseservice.dto.request.GlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.SpecificConfigReq;
import sum25.group03.warehouseservice.entity.Configuration;

@Mapper(componentModel = "spring")
public interface ConfigMapper {

    @Mapping(target = "active", expression = "java(true)")
    Configuration toEntity(SpecificConfigReq configDTO);
    @Mapping(target = "active", expression = "java(true)")
    GlobalConfiguration toEntity(GlobalConfigReq configDTO);

}
