package sum25.group03.testorderservice.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import sum25.group03.common.response.dtos.grpc.CleanTestOrderResponse;
import sum25.group03.common.response.dtos.grpc.CleanTestResultResponse;
import sum25.group03.testorder.grpc.*;
import sum25.group03.testorder.grpc.TestResultResponse;
import sum25.group03.testorderservice.dtos.request.TestOrderRequest;
import sum25.group03.testorderservice.dtos.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dtos.response.*;
import sum25.group03.testorderservice.dtos.response.TestOrderResponse;
import sum25.group03.testorderservice.entities.TestOrder;


import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {TestResultMapper.class, CommentMapper.class}
)
public interface TestOrderMapper {

    @Mapping(target = "testResults", source = "testResults")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "globalTestParameterId", source = "globalTestParameterId")
    @Mapping(target = "runDate", source = "runDate")
    @Mapping(target = "barcode", source = "barcode")
    @Mapping(target = "runBy", source = "runBy")
    TestOrderResponseDTO toResponseDto(TestOrder testOrder);

    List<TestOrderResponseDTO> toResponseDtoList(List<TestOrder> testOrders);

    @Mapping(target = "testResults", source = "testResults")
    @Mapping(target = "comments", source = "comments")
    TestOrderResponse toResponse(TestOrder testOrder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    @Mapping(target = "comments", ignore = true)
    TestOrder toEntity(TestOrderRequestDTO requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    @Mapping(target = "comments", ignore = true)
    TestOrder toEntityFrom(TestOrderRequest requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntity(TestOrderRequestDTO requestDto, @MappingTarget TestOrder testOrder);

    // to CleanTestOrderResponse
    @Mapping(target="testOrderId", source="id")
    @Mapping(target="barcode", source="barcode")
    @Mapping(target="testResults", source="testResults")
    CleanTestOrderResponse toCleanResponseDto(TestOrder testOrder);

    // manually mappings Page<TestOrder> to Page<TestOrderResponseDTO>
    default Page<TestOrderResponseDTO> toResponseDtoPage(Page<TestOrder> testOrders) {
        List<TestOrder> entities = testOrders.getContent();
        List<TestOrderResponseDTO> dtoList = toResponseDtoList(entities);
        return new PageImpl<>(dtoList, testOrders.getPageable(), testOrders.getTotalElements());
    }

    //--------------------------------------------------------------
    // Grpc CleanTestResultResponse to GrpcCleanTestResultResponse
    default GrpcCleanTestResultResponse toGrpcCleanTestResultResponseDto(CleanTestResultResponse dtoResponse) {
        return GrpcCleanTestResultResponse.newBuilder()
                .setTestResultId(dtoResponse.getTestResultId())
                .setParameterId(dtoResponse.getParameterId())
                .setParameterCode(dtoResponse.getParameterCode())
                .build();
    }

    // Map functions for gRPC methods get clean test order by test order id:
    default GrpcCleanTestOrderResponse toGrpcCleanTestOrderResponse(CleanTestOrderResponse response) {
        /*
         private List<CleanTestResultResponse> testResults;
         */
        return GrpcCleanTestOrderResponse.newBuilder()
                .setTestOrderId(response.getTestOrderId())
                .setBarcode(response.getBarcode())
                .addAllTestResults(response.getTestResults().stream()
                        .map(this::toGrpcCleanTestResultResponseDto).collect(Collectors.toList()))
                .build();
    }


    // ----------------- TestOrdersByMedicalRecordResponse mapper -----------------
    default TestOrdersByMedicalRecordResponse toGrpcMedicalRecordResponse(TestOrderResponseDTO dto) {
        if (dto == null) return null;

        TestOrdersByMedicalRecordResponse.Builder builder = TestOrdersByMedicalRecordResponse.newBuilder();

        builder.setId(dto.getId() != null ? dto.getId() : 0L);
        builder.setExternalMedicalRecordId(dto.getExternalMedicalRecordId() != null ? dto.getExternalMedicalRecordId() : 0L);
        builder.setPatientId(dto.getPatientId() != null ? dto.getPatientId() : 0L);
        builder.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : 0L);
        builder.setRunBy(dto.getRunBy() != null ? dto.getRunBy() : 0L);
        builder.setCode(dto.getCode() != null ? dto.getCode() : "");
        builder.setRunDate(dto.getRunDate() != null ? dto.getRunDate().toString() : "");
        builder.setStatus(dto.getStatus() != null ? dto.getStatus().name() : "");
        builder.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : "");
        builder.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toString() : "");
        builder.setBarcode(dto.getBarcode() != null ? dto.getBarcode() : "");
        builder.setTotalPrice(dto.getTotalPrice() != null ? dto.getTotalPrice() : 0L);


        if (dto.getTestResults() != null) {
            builder.addAllTestResults(dto.getTestResults().stream()
                    .map(this::toGrpcTestResult)
                    .collect(Collectors.toList()));
        }

        // map nested CommentResponse list safely
        if (dto.getComments() != null) {
            builder.addAllComments(dto.getComments().stream()
                    .map(this::toGrpcComment)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    // ----------------- TestResult mapper -----------------
    default TestResultResponse toGrpcTestResult(TestResultResponseDTO dto) {
        if (dto == null) return null;

        TestResultResponse.Builder builder = TestResultResponse.newBuilder();

        builder.setId(dto.getId() != null ? dto.getId() : 0L);
        builder.setTestOrderId(dto.getTestOrderId() != null ? dto.getTestOrderId() : 0L);
        builder.setInstrumentId(dto.getInstrumentId() != null ? dto.getInstrumentId() : 0L);
        builder.setParameterId(dto.getParameterId() != null ? dto.getParameterId() : 0L);
        builder.setParameterName(dto.getParameterName() != null ? dto.getParameterName() : "");
        builder.setFlagStatus(dto.getFlagStatus() != null ? dto.getFlagStatus() : "");
        builder.setStatus(dto.getStatus() != null ? dto.getStatus().name() : "");
        builder.setValue(dto.getValue() != null ? dto.getValue() : 0.0);
        builder.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : "");
        builder.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toString() : "");
        builder.setPrice(dto.getPrice() != null ? dto.getPrice() : 0L);

        // map nested comments inside TestResultResponse
        if (dto.getComments() != null) {
            builder.addAllComments(dto.getComments().stream()
                    .map(this::toGrpcComment)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    // ----------------- Comment mapper -----------------
    default CommentResponse toGrpcComment(CommentResponseDTO dto) {
        if (dto == null) return null;

        CommentResponse.Builder builder = CommentResponse.newBuilder();

        builder.setId(dto.getId() != null ? dto.getId() : 0L);
        builder.setTestOrderId(dto.getTestOrderId() != null ? dto.getTestOrderId() : 0L);
        builder.setTestResultId(dto.getTestResultId() != null ? dto.getTestResultId() : 0L);
        builder.setUserId(dto.getUserId() != null ? dto.getUserId() : 0L);
        builder.setCommentText(dto.getCommentText() != null ? dto.getCommentText() : "");
        builder.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : "");
        builder.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toString() : "");
        builder.setStatus(dto.getStatus() != null ? dto.getStatus().name() : "");

        return builder.build();
    }
}