package sum25.group03.testorderservice.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import sum25.group03.testorder.grpc.*;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseForInstrument;
import sum25.group03.testorderservice.dtos.response.CreationTestOrderResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@GrpcService
public class TestOrderServiceGrpcImpl extends TestOrderServiceGrpc.TestOrderServiceImplBase {

    @Autowired
    private TestOrderService testOrderService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

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
                        .setCode(testOrder.getCode() != null ? testOrder.getCode() : "")
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
                    .setCode(createdOrder.getCode() != null ? createdOrder.getCode() : "")
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
}
