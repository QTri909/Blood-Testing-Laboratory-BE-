package sum25.group03.testorderservice.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import sum25.group03.testorderservice.grpc.GetLatestTestOrderRequest;
import sum25.group03.testorderservice.grpc.TestOrderResponse;
import sum25.group03.testorderservice.grpc.TestOrderServiceGrpc;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.repositories.TestOrderRepository;

import java.time.format.DateTimeFormatter;

@GrpcService
@RequiredArgsConstructor
public class TestOrderGrpcServer extends TestOrderServiceGrpc.TestOrderServiceImplBase {

    private final TestOrderRepository testOrderRepository;

    @Override
    public void getLatestTestOrderByPatientId(
            GetLatestTestOrderRequest request,
            StreamObserver<TestOrderResponse> responseObserver) {

        Long patientId = request.getPatientId();

        var latestOrderOpt = testOrderRepository.findTopByPatientIdOrderByCreatedAtDesc(patientId);

        if (latestOrderOpt.isEmpty()) {
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription("No test order found for patientId " + patientId)
                            .asRuntimeException()
            );
            return;
        }

        TestOrder order = latestOrderOpt.get();

        TestOrderResponse response = TestOrderResponse.newBuilder()
                .setId(order.getId())
                .setBarcode(order.getBarcode() == null ? "" : order.getBarcode())
                .setType(order.getType() != null ? order.getType().name() : "")
                .setPatientId(order.getPatientId())
                .setStatus(order.getStatus() != null ? order.getStatus().name() : "")
                .setCreatedAt(order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
