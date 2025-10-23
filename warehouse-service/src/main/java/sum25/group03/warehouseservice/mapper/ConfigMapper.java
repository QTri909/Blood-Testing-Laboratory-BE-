package sum25.group03.warehouseservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.entity.Configurations;

@Mapper(componentModel = "spring")
public interface ConfigMapper {
    @Mapping(target = "configType", expression = "java(sum25.group03.warehouseservice.entity.enums.ConfigType.GLOBAL)")
    Configurations toEntity(ConfigReq configDTO);


}
