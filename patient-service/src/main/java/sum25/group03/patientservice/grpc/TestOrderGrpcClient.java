package sum25.group03.patientservice.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestOrderGrpcClient {

    @GrpcClient("test-order-service")
    private TestOrderServiceGrpc.TestOrderServiceBlockingStub blockingStub;

    public TestOrderResponse getLatestTestOrderByPatientId(Long patientId) {
        GetLatestTestOrderRequest request = GetLatestTestOrderRequest.newBuilder()
                .setPatientId(patientId)
                .build();

        return blockingStub.getLatestTestOrderByPatientId(request);
    }
}
