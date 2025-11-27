package sum25.group03.testorderservice.grpc;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.dtos.request.GrpcMappingPatientAndCreatorIdRequest;
import sum25.group03.testorderservice.dtos.response.GrpcMappingPatientAndCreatorIdResponse;
import sum25.group03.testorderservice.grpc.GetPatientByIdRequest;
import sum25.group03.testorderservice.grpc.GetPatientByIdResponse;
import sum25.group03.testorderservice.grpc.PatientServiceGrpc;
import sum25.group03.testorderservice.mapper.GrpcUserSnapshotMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientGrpcClient {

    private final GrpcUserSnapshotMapper grpcUserSnapshotMapper;
    @GrpcClient("patient-service")
    private PatientServiceGrpc.PatientServiceBlockingStub patientStub;

    public GetPatientByIdResponse getPatientById(Long externalUserId) {
        try {
            GetPatientByIdRequest request = GetPatientByIdRequest.newBuilder()
                    .setExternalUserId(externalUserId)
                    .build();

            return patientStub.getPatientById(request);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to fetch patient info from gRPC", e);
        }
    }

    public CreatedMedicalRecordResponse createdMedicalRecordResponse(Long createdBy, Long patientId) {
        try {
            AutoCreateMedicalRecordRequest request = AutoCreateMedicalRecordRequest.newBuilder()
                    .setCreatedBy(createdBy)
                    .setPatientId(patientId)
                    .build();

            return patientStub.autoCreateMedicalRecord(request);
        } catch (StatusRuntimeException e) {
            io.grpc.Status status = io.grpc.Status.fromThrowable(e);

            log.error("gRPC failed. Code={}, Description={}, Cause={}",
                    status.getCode(),
                    status.getDescription(),
                    e.getCause(),
                    e
            );

            String detailedMessage = String.format(
                    "gRPC error: %s - %s",
                    status.getCode(),
                    status.getDescription()
            );

            throw new RuntimeException(detailedMessage, e);
        }
    }

    public EmptyMsg assignPatientIdToMedicalRecord(Long medicalRecordId, Long patientId) {
        try {
            AssignPatientIdToMedicalRecordRequest request = AssignPatientIdToMedicalRecordRequest.newBuilder()
                    .setMedicalRecordId(medicalRecordId)
                    .setPatientId(patientId)
                    .build();

            return patientStub.assignPatientIdToMedicalRecord(request);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to assign patient ID to medical record via gRPC", e);
        }
    }

    public GrpcMappingPatientAndCreatorIdResponse mappingPatientIdAndCreatorIdToTheirName(
            List<Long> patientIds, List<Long> creatorIds
    ) {
        try {
            GrpcMappingPatientAndCreatorIdRequest dtoRequest = GrpcMappingPatientAndCreatorIdRequest.builder()
                    .patientIds(patientIds)
                    .creatorIds(creatorIds)
                    .build();

            MappingPatientIdAndCreatorIdToTheirNameRequest request = grpcUserSnapshotMapper.toGrpcMappingRequest(dtoRequest);

            MappingPatientIdAndCreatorIdToTheirNameResponse response = patientStub.mappingPatientIdAndCreatorIdToTheirName(request);
            return grpcUserSnapshotMapper.fromGrpcMappingResponse(response);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to map patient and creator IDs to names via gRPC", e);
        }
    }
}
