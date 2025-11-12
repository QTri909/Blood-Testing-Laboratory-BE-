package sum25.group03.patientservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import sum25.group03.patientservice.grpc.*;
import sum25.group03.patientservice.grpc.dtos.GrpcCommentDTO;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderDTO;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderFullFieldDTO;
import sum25.group03.patientservice.grpc.dtos.GrpcTestResultDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrpcTestOrderMapper {

    GrpcTestOrderMapper INSTANCE = Mappers.getMapper(GrpcTestOrderMapper.class);

    GrpcTestOrderDTO toDto(TestOrderResponse testOrderResponse);

    // define nested mappers:
    GrpcTestResultDTO toDto(TestResultResponse testResultResponse);
    GrpcCommentDTO toDto(CommentResponse commentResponse);

    @Mapping(target = "testResults", source = "testResultsList")
    @Mapping(target = "comments", source = "commentsList")
    GrpcTestOrderFullFieldDTO toFullFieldDto(TestOrdersByMedicalRecordResponse testOrdersByMedicalRecordResponse);

    List<GrpcTestOrderFullFieldDTO> toFullFieldDtoList(List<TestOrdersByMedicalRecordResponse> responseList);
}
