package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dto.request.ParameterRequestDTO;
import sum25.group03.testorderservice.dto.response.ParameterResponseDTO;
import sum25.group03.testorderservice.entity.Parameter;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParameterMapper {

    ParameterResponseDTO toResponseDto(Parameter parameter);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    Parameter toEntity(ParameterRequestDTO requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    void updateEntity(ParameterRequestDTO requestDto, @MappingTarget Parameter parameter);
}