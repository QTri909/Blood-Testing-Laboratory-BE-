package sum25.group03.testorderservice.grpc;


import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import sum25.group03.testorder.grpc.*;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;
import sum25.group03.testorderservice.repositories.TestOrderRepository;

import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseForInstrument;
import sum25.group03.testorderservice.dtos.response.CreationTestOrderResponse;

import java.time.format.DateTimeFormatter;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TestOrderGrpcServer extends TestOrderServiceGrpc.TestOrderServiceImplBase {

    private final TestOrderRepository testOrderRepository;
    private final TestOrderService testOrderService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

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
    public void getTestOrderByBarcode(GetTestOrderByBarcodeRequest request,
                                      StreamObserver<GetTestOrderByBarcodeResponse> responseObserver) {
        try {
            log.info("gRPC: GetTestOrderByBarcode called for barcode: {}", request.getBarcode());
            TestOrderResponseForInstrument testOrder = testOrderService.findLatestByBarcode(request.getBarcode());

            if (testOrder != null) {
                GetTestOrderByBarcodeResponse response = GetTestOrderByBarcodeResponse.newBuilder()
                        .setId(testOrder.getId() != null ? testOrder.getId() : 0L)
                        .setExternalMedicalRecordId(
                                testOrder.getExternalMedicalRecordId() != null ? testOrder.getExternalMedicalRecordId() : 0L
                        )
                        .setCode(testOrder.getCode() != null ? testOrder.getCode().toString() : "")
                        .setPatientId(testOrder.getPatientId() != null ? testOrder.getPatientId() : 0L)
                        .setCreatedBy(testOrder.getCreatedBy() != null ? testOrder.getCreatedBy() : 0L)
                        .setRunBy(testOrder.getRunBy() != null ? testOrder.getRunBy() : 0L)
                        .setBarcode(testOrder.getBarcode() != null ? testOrder.getBarcode() : "")
                        .setTestType(testOrder.getTestType() != null ? testOrder.getTestType() : "")
                        .setRunDate(testOrder.getRunDate() != null ? testOrder.getRunDate().toString() : "")

                        .setStatus(testOrder.getStatus() != null ? testOrder.getStatus().toString() : "")
                        .setCreatedAt(testOrder.getCreatedAt() != null ? testOrder.getCreatedAt().toString() : "")
                        .setUpdatedAt(testOrder.getUpdatedAt() != null ? testOrder.getUpdatedAt().toString() : "")
                        .setFound(true)
                        .setMessage("Success")
                        .build();

                responseObserver.onNext(response);
            } else {
                GetTestOrderByBarcodeResponse response = GetTestOrderByBarcodeResponse.newBuilder()
                        .setFound(false)
                        .setMessage("Test order not found")
                        .build();

                responseObserver.onNext(response);
            }

            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in getTestOrderByBarcode", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asException());
        }
    }


    @Override
    public void createUnmatchedOrder(CreateUnmatchedOrderRequest request,
                                     StreamObserver<CreateUnmatchedOrderResponse> responseObserver) {
        try {
            log.info("gRPC: CreateUnmatchedOrder called for barcode: {}", request.getBarcode());
            CreationTestOrderResponse createdOrder = testOrderService.createTestOrderForExternalSystem(request.getBarcode());

            CreateUnmatchedOrderResponse response = CreateUnmatchedOrderResponse.newBuilder()
                    .setId(createdOrder.getId() != null ? createdOrder.getId() : 0L)
                    .setCode(createdOrder.getCode() != null ? createdOrder.getCode().toString() : "")
                    .setBarcode(createdOrder.getBarcode() != null ? createdOrder.getBarcode() : "")
                    .setStatus(createdOrder.getStatus() != null ? createdOrder.getStatus().toString() : "")
                    .setCreatedAt(createdOrder.getCreatedAt() != null ? createdOrder.getCreatedAt().toString() : "")
                    .setSuccess(true)
                    .setMessage("Unmatched order created successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in createUnmatchedOrder", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asException());
        }
    }

    // Ghi chú: Hãy đảm bảo file .proto của bạn định nghĩa CẢ 3 phương thức này
    // (getLatestTestOrderByPatientId, getTestOrderByBarcode, createUnmatchedOrder)
    // bên trong 'service TestOrderService'.

}