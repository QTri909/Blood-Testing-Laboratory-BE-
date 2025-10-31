package sum25.group03.warehouseservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.dto.response.InstrumentResponse;
import sum25.group03.warehouseservice.entity.Instrument;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
//    @Mapping(target = "status", expression = "java(sum25.group03.warehouseservice.entity.enums.InstrumentStatus.ACTIVE)")
//    Instrument toEntity(InstrumentReq instrument);
//
//    InstrumentResponse toResponse(Instrument instrument);

//    @AfterMapping
//    default void fillManufacturerName(@MappingTarget InstrumentResponse dto, Instrument entity) {
//        if(entity.getManufacturer() != null) {
//            dto.setManufacturerName(entity.getManufacturer());
//        }
//    }
}
