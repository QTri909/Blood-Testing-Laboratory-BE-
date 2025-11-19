package sum25.group03.warehouseservice.mapper;

import org.mapstruct.Mapper;
import sum25.group03.warehouseservice.dto.response.NormalRangeRes;
import sum25.group03.warehouseservice.entity.NormalRange;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NormalRangeMapper {
    List<NormalRangeRes> toResponse(List<NormalRange> normalRanges);
    NormalRangeRes toResponse(NormalRange normalRange);
}
