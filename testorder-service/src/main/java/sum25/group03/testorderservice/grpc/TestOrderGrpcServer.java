package sum25.group03.testorderservice.grpc;

import sum25.group03.common.response.dtos.grpc.CleanTestOrderResponse;
import sum25.group03.common.response.dtos.grpc.ParameterGrpc;
import sum25.group03.common.response.dtos.grpc.ParameterGrpcResponse;
import sum25.group03.testorder.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import sum25.group03.testorderservice.dtos.response.CreationTestOrderResponse;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseForInstrument;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.mapper.ParameterMapper;
import sum25.group03.testorderservice.mapper.TestOrderMapper;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.services.interfaces.ParameterService;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TestOrderGrpcServer extends TestOrderServiceGrpc.TestOrderServiceImplBase {

    private final TestOrderRepository testOrderRepository;
    private final TestOrderService testOrderService;
    private final TestOrderMapper testOrderMapper;
    private final ParameterMapper parameterMapper;
    private final ParameterService parameterService;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;


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
                .setCreatedAt(order.getCreatedAt().format(ISO_FORMATTER)) // 4. Sử dụng formatter
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
                GetTestOrderByBarcodeResponse.Builder builder = GetTestOrderByBarcodeResponse.newBuilder();

                builder.setId(testOrder.getId() != null ? testOrder.getId() : 0L)
                        .setExternalMedicalRecordId(testOrder.getExternalMedicalRecordId() != null ? testOrder.getExternalMedicalRecordId() : 0L)
                        .setCode(testOrder.getCode() != null ? testOrder.getCode().toString() : "")
                        .setPatientId(testOrder.getPatientId() != null ? testOrder.getPatientId() : 0L)
                        .setCreatedBy(testOrder.getCreatedBy() != null ? testOrder.getCreatedBy() : 0L)
                        .setRunBy(testOrder.getRunBy() != null ? testOrder.getRunBy() : 0L)
                        .setBarcode(testOrder.getBarcode() != null ? testOrder.getBarcode() : "")
                        .setTestType(testOrder.getTestType() != null ? testOrder.getTestType() : "")
                        .setStatus(testOrder.getStatus() != null ? testOrder.getStatus().toString() : "")
                        .setFound(true)
                        .setMessage("Success");


                if (testOrder.getRunDate() != null) {
                    builder.setRunDate(testOrder.getRunDate().format(ISO_FORMATTER));
                }
                if (testOrder.getCreatedAt() != null) {
                    builder.setCreatedAt(testOrder.getCreatedAt().format(ISO_FORMATTER));
                }
                if (testOrder.getUpdatedAt() != null) {
                    builder.setUpdatedAt(testOrder.getUpdatedAt().format(ISO_FORMATTER));
                }

                responseObserver.onNext(builder.build());
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

            CreateUnmatchedOrderResponse.Builder builder = CreateUnmatchedOrderResponse.newBuilder();

            builder.setId(createdOrder.getId() != null ? createdOrder.getId() : 0L)
                    .setCode(createdOrder.getCode() != null ? createdOrder.getCode().toString() : "")
                    .setBarcode(createdOrder.getBarcode() != null ? createdOrder.getBarcode() : "")
                    .setStatus(createdOrder.getStatus() != null ? createdOrder.getStatus().toString() : "")
                    .setSuccess(true)
                    .setMessage("Unmatched order created successfully");

            if (createdOrder.getCreatedAt() != null) {
                builder.setCreatedAt(createdOrder.getCreatedAt().format(ISO_FORMATTER));
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in createUnmatchedOrder", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asException());
        }
    }


    @Override
    public void getAllTestOrdersByMedicalRecordId(MedicalRecordIdRequest request, StreamObserver<TestOrdersByMedicalRecordResponseList> responseObserver) {
        Long medicalRecordId = request.getMedicalRecordId();
        Long viewerId = request.getViewerId();

        List<TestOrderResponseDTO> testOrders = testOrderService.getAllTestOrdersByMedicalRecordId(medicalRecordId, viewerId);

        TestOrdersByMedicalRecordResponseList.Builder responseListBuilder = TestOrdersByMedicalRecordResponseList.newBuilder();
        for (TestOrderResponseDTO testOrder : testOrders) {
            // Giả sử testOrderMapper.toGrpcMedicalRecordResponse đã xử lý null an toàn
            TestOrdersByMedicalRecordResponse mappedOrder = testOrderMapper.toGrpcMedicalRecordResponse(testOrder);
            responseListBuilder.addTestOrders(mappedOrder);
        }

        responseObserver.onNext(responseListBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getCleanTestOrderById(GetCleanTestOrderByIdRequest request, StreamObserver<GrpcCleanTestOrderResponse> responseObserver) {

        try {
            long testOrderId = request.getTestOrderId();
            if (testOrderId <= 0L)
                throw new RuntimeException("testOrderId is null or zero or negative");

            CleanTestOrderResponse cleanResponse = testOrderService.getTestOrderByIdCleanData(testOrderId);
            GrpcCleanTestOrderResponse grpcResponse = testOrderMapper.toGrpcCleanTestOrderResponse(cleanResponse);

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch(Exception e) {
            log.error("Error in getCleanTestOrderById", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asException());
        }
    }

    public void syncParameter(
            SyncParameterRequest request,
            StreamObserver<SyncParameterResponse> responseObserver
    ) {
        // mapping get ParameterGrpc for handling
        ParameterGrpc parameterGrpc = parameterMapper.toParameterFromGrpc(request);

        // handle sync parameter
        ParameterGrpcResponse response = parameterService.syncParameterFromWarehouse(parameterGrpc);

        // map to SyncParameterResponse for gRPC response
        SyncParameterResponse grpcResponse = parameterMapper.toGrpcResponseFromParameter(response);

        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();
    }

}