package sum25.group03.patientservice.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderDTO;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderFullFieldDTO;
import sum25.group03.patientservice.mapper.GrpcTestOrderMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestOrderGrpcClient {

    private final GrpcTestOrderMapper testOrderMapper;

    @GrpcClient("test-order-service")
    private TestOrderServiceGrpc.TestOrderServiceBlockingStub blockingStub;

    public GrpcTestOrderDTO getLatestTestOrderByPatientId(Long patientId) {

        // send gRPC request and receive response
        TestOrderResponse fetchedResponse = blockingStub.getLatestTestOrderByPatientId(
                GetLatestTestOrderRequest.newBuilder().setPatientId(patientId).build()
        );

        // map response to DTO
        return testOrderMapper.toDto(fetchedResponse);
    }

    public List<GrpcTestOrderFullFieldDTO> getAllTestOrdersByMedicalRecordId(
            Long medicalRecordId, Long viewerId
    ) {
        // send gRPC request and receive response
        MedicalRecordIdRequest request = MedicalRecordIdRequest.newBuilder()
                .setMedicalRecordId(medicalRecordId)
                .setViewerId(viewerId).build();
        TestOrdersByMedicalRecordResponseList fetchedResponse = blockingStub.getAllTestOrdersByMedicalRecordId(request);

        // map response to DTO
        return testOrderMapper.toFullFieldDtoList(fetchedResponse.getTestOrdersList());
    }
}
