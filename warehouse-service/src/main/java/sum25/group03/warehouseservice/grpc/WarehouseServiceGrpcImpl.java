package sum25.group03.warehouseservice.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import sum25.group03.warehouse.grpc.*;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.dto.response.ReagentResponseForInstrument;
import sum25.group03.warehouseservice.dto.response.ReagentValidationResponse;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;
import sum25.group03.warehouseservice.service.reagent.ReagentService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@GrpcService
public class WarehouseServiceGrpcImpl extends WarehouseServiceGrpc.WarehouseServiceImplBase {

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private ReagentService reagentService;

    @Override
    public void checkInstrumentStatus(CheckInstrumentStatusRequest request,
                                      StreamObserver<CheckInstrumentStatusResponse> responseObserver) {
        try {
            log.info("gRPC: CheckInstrumentStatus called for instrument ID: {}", request.getInstrumentId());


            InstrumentStatusResponse isActive = instrumentService.getInstrumentStatus(request.getInstrumentId());


            CheckInstrumentStatusResponse response = CheckInstrumentStatusResponse.newBuilder()
                    .setIsActive(isActive.isActive())
                    .setStatus(isActive.getStatus().toString())
                    .setMessage("Success")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in checkInstrumentStatus", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void validateReagent(ValidateReagentRequest request,
                                StreamObserver<ValidateReagentResponse> responseObserver) {
        try {
            log.info("gRPC: ValidateReagent called for lot number: {}", request.getLotNumber());


            ReagentValidationResponse reagentValidationResponse = reagentService.validateReagent(request.getLotNumber(), request.getRequiredVolume());

            ValidateReagentResponse response = ValidateReagentResponse.newBuilder()
                    .setReagentId(reagentValidationResponse.getReagentId())
                    .setReagentName(reagentValidationResponse.getReagentName())
                    .setLotNumber(reagentValidationResponse.getLotNumber())
                    .setUnit(reagentValidationResponse.getUnit())
                    .setCatalogNumber(reagentValidationResponse.getCatalogNumber())
                    .setExpirationDate(reagentValidationResponse.getExpirationDate().toString())
                    .setIsValid(reagentValidationResponse.isValid())
                    .setIsInInventory(reagentValidationResponse.isInInventory())
                    .setIsNotExpired(reagentValidationResponse.isNotExpired())
                    .setMessage(reagentValidationResponse.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in validateReagent", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void listReagents(com.google.protobuf.Empty request, StreamObserver<ListReagentsResponse> responseObserver) {
        try {
            log.info("gRPC: ListReagents called by instrument service");


            List<ReagentResponseForInstrument> reagents = reagentService.listReagentsForInstrument();


            ListReagentsResponse.Builder responseBuilder = ListReagentsResponse.newBuilder();
            reagents.forEach(reagent -> {
                ReagentInfo info = ReagentInfo.newBuilder()
                        .setReagentId(reagent.getReagentId())
                        .setUnit(reagent.getUnit())
                        .setReagentName(reagent.getReagentName())
                        .setUsageMax(reagent.getUsageMax())
                        .setUsageMin(reagent.getUsageMin())
                        .build();
                responseBuilder.addReagents(info);
            });

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in listReagents", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asException());
        }
    }
}
