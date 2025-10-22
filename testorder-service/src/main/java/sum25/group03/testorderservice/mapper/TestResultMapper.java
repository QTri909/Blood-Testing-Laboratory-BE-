package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dto.request.TestResultRequestDTO;
import sum25.group03.testorderservice.dto.response.TestResultResponseDTO;
import sum25.group03.testorderservice.entity.TestResult;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {ReagentUsedMapper.class, CommentMapper.class})
public interface TestResultMapper {

    @Mapping(source = "testOrder.id", target = "testOrderId")
    @Mapping(source = "parameter.id", target = "parameterId")
    @Mapping(source = "parameter.name", target = "parameterName")
    TestResultResponseDTO toResponseDto(TestResult testResult);

    @Mapping(source = "testOrderId", target = "testOrder.id")
    @Mapping(source = "parameterId", target = "parameter.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reagentsUsed", ignore = true)
    @Mapping(target = "comments", ignore = true)
    TestResult toEntity(TestResultRequestDTO requestDto);

    @Mapping(source = "testOrderId", target = "testOrder.id")
    @Mapping(source = "parameterId", target = "parameter.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reagentsUsed", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntity(TestResultRequestDTO requestDto, @MappingTarget TestResult testResult);
}