package sum25.group03.warehouseservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import sum25.group03.warehouseservice.dto.response.ParameterRes;
import sum25.group03.warehouseservice.entity.TestParameter;

@Mapper(componentModel = "spring")
public interface ParameterMapper {
//    @Mapping(source = "gender", target = "gender", qualifiedByName = "enumToString")
    @Mapping(source = "status", target = "status", qualifiedByName = "enumToString")
    ParameterRes toDto(TestParameter parameter);

    @Named("enumToString")
    default String enumToString(Enum<?> e) {
        return e != null ? e.name() : null;
    }
}
