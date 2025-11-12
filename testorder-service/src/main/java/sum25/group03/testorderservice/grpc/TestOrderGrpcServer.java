package sum25.group03.testorderservice.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.grpc.*;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.grpc.GetLatestTestOrderRequest;
import sum25.group03.testorderservice.grpc.MedicalRecordIdRequest;
import sum25.group03.testorderservice.grpc.TestOrderResponse;
import sum25.group03.testorderservice.grpc.TestOrderServiceGrpc;
import sum25.group03.testorderservice.grpc.TestOrdersByMedicalRecordResponse;
import sum25.group03.testorderservice.grpc.TestOrdersByMedicalRecordResponseList;
import sum25.group03.testorderservice.mapper.TestOrderMapper;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class TestOrderGrpcServer extends TestOrderServiceGrpc.TestOrderServiceImplBase {

    private final TestOrderRepository testOrderRepository;
    private final TestOrderMapper testOrderMapper;
    private final TestOrderService testOrderService;

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

    @Override
    public void getAllTestOrdersByMedicalRecordId(MedicalRecordIdRequest request, StreamObserver<TestOrdersByMedicalRecordResponseList> responseObserver) {
        Long medicalRecordId = request.getMedicalRecordId();
        Long viewerId = request.getViewerId();

        List<TestOrderResponseDTO> testOrders = testOrderService.getAllTestOrdersByMedicalRecordId(medicalRecordId, viewerId);

        // parse List<TestOrderResponseDTO> to TestOrdersByMedicalRecordResponseList
        TestOrdersByMedicalRecordResponseList.Builder responseListBuilder = TestOrdersByMedicalRecordResponseList.newBuilder();
        for (TestOrderResponseDTO testOrder: testOrders) {
            TestOrdersByMedicalRecordResponse mappedOrder = testOrderMapper.toGrpcMedicalRecordResponse(testOrder);
            responseListBuilder.addTestOrders(mappedOrder);
        }

        responseObserver.onNext(responseListBuilder.build());
        responseObserver.onCompleted();
    }
}
