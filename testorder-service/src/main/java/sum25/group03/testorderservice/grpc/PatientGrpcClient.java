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
}
