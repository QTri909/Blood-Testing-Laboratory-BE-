package sum25.group03.warehouseservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.entity.Instrument;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    @Mapping(target = "status", expression = "java(sum25.group03.warehouseservice.entity.enums.InstrumentStatus.ACTIVE)")
    Instrument toEntity(InstrumentReq instrument);
}
