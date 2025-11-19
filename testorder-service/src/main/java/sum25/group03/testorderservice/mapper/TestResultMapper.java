package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.common.response.dtos.grpc.CleanTestResultResponse;
import sum25.group03.testorder.grpc.GrpcCleanTestResultResponse;
import sum25.group03.testorderservice.dtos.request.TestResultRequestDTO;
import sum25.group03.testorderservice.dtos.response.TestResultResponseDTO;
import sum25.group03.testorderservice.entities.TestResult;


import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {CommentMapper.class})
public interface TestResultMapper {

    @Mapping(source = "testOrder.id", target = "testOrderId")
    @Mapping(source = "parameter.id", target = "parameterId")
    @Mapping(source = "parameter.name", target = "parameterName")
//    @Mapping( source = "status", target = "testResultStatus")
    TestResultResponseDTO toResponseDto(TestResult testResult);

    List<TestResultResponseDTO> toResponseDtos(List<TestResult> testResults);

    @Mapping(source = "testOrderId", target = "testOrder.id")
    @Mapping(source = "parameterId", target = "parameter.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    TestResult toEntity(TestResultRequestDTO requestDto);

    @Mapping(source = "testOrderId", target = "testOrder.id")
    @Mapping(source = "parameterId", target = "parameter.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntity(TestResultRequestDTO requestDto, @MappingTarget TestResult testResult);

    // to CleanTestResultResponse
    @Mapping(target="testResultId", source="id")
    @Mapping(target="parameterId", source="parameter.id")
    @Mapping(target="parameterCode", source="parameter.paramCode")
    CleanTestResultResponse toCleanResponseDto(TestResult testResult);

    // List<TestResult> to List<CleanTestResultResponse>
    List<CleanTestResultResponse> toCleanResponseDtoList(List<TestResult> testResults);
}