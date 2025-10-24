package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dto.request.ReagentUsedRequestDTO;
import sum25.group03.testorderservice.dto.response.ReagentUsedResponseDTO;
import sum25.group03.testorderservice.entity.ReagentUsed;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReagentUsedMapper {

    ReagentUsedResponseDTO toResponseDto(ReagentUsed reagentUsed);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ReagentUsed toEntity(ReagentUsedRequestDTO requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ReagentUsedRequestDTO requestDto, @MappingTarget ReagentUsed reagentUsed);
}