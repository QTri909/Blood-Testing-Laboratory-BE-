package sum25.group03.instrumentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sum25.group03.instrumentservice.client.response.ReagentResponse;
import sum25.group03.instrumentservice.client.response.ReagentValidationResponse;
import sum25.group03.instrumentservice.exception.WarehouseServiceException;
import sum25.group03.warehouse.grpc.WarehouseServiceGrpc;
import sum25.group03.warehouse.grpc.CheckInstrumentStatusRequest;
import sum25.group03.warehouse.grpc.CheckInstrumentStatusResponse;
import sum25.group03.warehouse.grpc.ValidateReagentRequest;
import sum25.group03.warehouse.grpc.ValidateReagentResponse;
import sum25.group03.warehouse.grpc.ListReagentsResponse;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceClient {
    private final WarehouseServiceGrpc.WarehouseServiceBlockingStub warehouseServiceStub;

    public boolean checkInstrumentStatus(Long instrumentId) {
        try {
            log.info("Checking instrument status via gRPC: {}", instrumentId);

            CheckInstrumentStatusRequest request = CheckInstrumentStatusRequest.newBuilder()
                    .setInstrumentId(instrumentId)
                    .build();

            CheckInstrumentStatusResponse response = warehouseServiceStub.checkInstrumentStatus(request);

            boolean isActive = response.getIsActive();
            log.info("Instrument {} status: {} (Active: {})", instrumentId, response.getStatus(), isActive);
            return isActive;

        } catch (StatusRuntimeException e) {
            log.error("gRPC error checking instrument status for ID: {}", instrumentId, e);
            throw new WarehouseServiceException(
                    "Failed to check instrument status from Warehouse Service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error communicating with Warehouse Service for instrument ID: {}", instrumentId, e);
            throw new WarehouseServiceException(
                    "Failed to check instrument status from Warehouse Service: " + e.getMessage(), e);
        }
    }

    public ReagentValidationResponse validateReagent(String lotNumber, Double requiredVolume) {
        try {
            log.info("Validating reagent via gRPC: lotNumber={}, requiredVolume={}", lotNumber, requiredVolume);

            ValidateReagentRequest request = ValidateReagentRequest.newBuilder()
                    .setLotNumber(lotNumber)
                    .setRequiredVolume(requiredVolume)
                    .build();

            ValidateReagentResponse response = warehouseServiceStub.validateReagent(request);

            ReagentValidationResponse result = new ReagentValidationResponse();
            result.setReagentId(response.getReagentId());
            result.setReagentName(response.getReagentName());
            result.setLotNumber(response.getLotNumber());
            result.setUnit(response.getUnit());
            result.setCatalogNumber(response.getCatalogNumber());
            result.setValid(response.getIsValid());
            result.setInInventory(response.getIsInInventory());
            result.setNotExpired(response.getIsNotExpired());
            result.setMessage(response.getMessage());
//            if (response.getExpirationDate() != null && !response.getExpirationDate().isEmpty()) {
//                result.setExpirationDate(java.time.LocalDate.parse(response.getExpirationDate()));
//            }

            log.info("Reagent validation result - Valid: {}, Message: {}", result.isValid(), result.getMessage());
            return result;

        } catch (StatusRuntimeException e) {
            log.error("gRPC error validating reagent batch: {}", lotNumber, e);
            throw new WarehouseServiceException(
                    "Failed to validate reagent from Warehouse Service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error validating reagent from Warehouse Service for batch: {}", lotNumber, e);
            throw new WarehouseServiceException(
                    "Failed to validate reagent from Warehouse Service: " + e.getMessage(), e);
        }
    }

    public List<ReagentResponse> reagentResponseReagentList() {
        try {
            log.info("Listing reagents via gRPC");

            ListReagentsResponse response = warehouseServiceStub.listReagents(
                    com.google.protobuf.Empty.getDefaultInstance()
            );

            List<ReagentResponse> responseList = response.getReagentsList().stream()
                    .map(item -> {
                        ReagentResponse reagent = new ReagentResponse();
                        reagent.setReagentId(item.getReagentId());
                        reagent.setUnit(item.getUnit());
                        reagent.setReagentName(item.getReagentName());
                        reagent.setUsageMax(item.getUsageMax());
                        reagent.setUsageMin(item.getUsageMin());
                        return reagent;
                    })
                    .collect(Collectors.toList());

            log.info("Retrieved {} reagents from Warehouse Service", responseList.size());
            return responseList;

        } catch (StatusRuntimeException e) {
            log.error("gRPC error listing reagents", e);
            throw new WarehouseServiceException(
                    "Failed to list reagents from Warehouse Service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error listing reagents from Warehouse Service", e);
            throw new WarehouseServiceException(
                    "Failed to list reagents from Warehouse Service: " + e.getMessage(), e);
        }
    }
}
