package sum25.group03.warehouseservice.mapper;

import org.mapstruct.*;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateConfigReq;
import sum25.group03.warehouseservice.dto.response.ConfigRes;
import sum25.group03.warehouseservice.entity.Configuration;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConfigMapper {

    @Mapping(target = "active", expression = "java(true)")
    Configuration toEntity(ConfigReq configDTO);

    @Mapping(target = "configurationId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateConfigReq configDTO, @MappingTarget Configuration configuration);

    List<ConfigRes> toDto(List<Configuration> configurations);
    ConfigRes toDto(Configuration configuration);
}
