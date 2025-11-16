package sum25.group03.testorderservice.grpc;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.grpc.GetPatientByIdRequest;
import sum25.group03.testorderservice.grpc.GetPatientByIdResponse;
import sum25.group03.testorderservice.grpc.PatientServiceGrpc;

@Service
@RequiredArgsConstructor
public class PatientGrpcClient {

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
            throw new RuntimeException("Failed to create medical record via gRPC", e);
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
}
