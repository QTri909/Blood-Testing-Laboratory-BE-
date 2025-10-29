package sum25.group03.warehouseservice.mapper;

import org.mapstruct.Mapper;
import sum25.group03.warehouseservice.dto.response.ReagentForInstrumentRes;
import sum25.group03.warehouseservice.entity.Reagents;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReagentMapper {
    List<ReagentForInstrumentRes> toDto(List<Reagents> reagents);
    ReagentForInstrumentRes toDto(Reagents reagent);
}
