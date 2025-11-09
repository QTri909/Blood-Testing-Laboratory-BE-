package sum25.group03.testorderservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mapstruct.*;
import sum25.group03.testorderservice.dtos.request.KafkaParameterRequestDTO;
import sum25.group03.testorderservice.dtos.request.ParameterRequestDTO;
import sum25.group03.testorderservice.dtos.response.ParameterResponseDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.enums.ParameterGender;
import sum25.group03.testorderservice.enums.ParameterStatus;

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

    Parameter fromKafkaDto(KafkaParameterRequestDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromKafkaDto(KafkaParameterRequestDTO dto, @MappingTarget Parameter parameter);
}