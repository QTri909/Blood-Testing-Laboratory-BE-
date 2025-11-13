package sum25.group03.instrumentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sum25.group03.instrumentservice.client.response.CreationTestOrderResponse;
import sum25.group03.instrumentservice.client.response.TestOrderResponse;
import sum25.group03.testorder.grpc.*;
import io.grpc.StatusRuntimeException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestOrderServiceClient {
    private final TestOrderServiceGrpc.TestOrderServiceBlockingStub testOrderServiceStub;

    public TestOrderResponse getTestOrderByBarcode(String barcode) {
        try {
            log.info("Fetching test order by barcode via gRPC: {}", barcode);
            GetTestOrderByBarcodeRequest request = GetTestOrderByBarcodeRequest.newBuilder()
                    .setBarcode(barcode)
                    .build();
            GetTestOrderByBarcodeResponse response = testOrderServiceStub.getTestOrderByBarcode(request);
            if (response == null || !response.getFound()) {
                log.warn("Test Order Service returned not found for barcode: {}", barcode);
                return null;
            }

            TestOrderResponse result = new TestOrderResponse();
            result.setId(response.getId());
            result.setExternalMedicalRecordId(response.getExternalMedicalRecordId());
            result.setCode(response.getCode());
            result.setPatientId(response.getPatientId());
            result.setCreatedBy(response.getCreatedBy());
            result.setRunBy(response.getRunBy());
            result.setBarcode(response.getBarcode());
            result.setTestType(response.getTestType());
            result.setStatus(response.getStatus());
            if (response.getRunDate() != null && !response.getRunDate().isEmpty()) {
                result.setRunDate(LocalDate.parse(response.getRunDate()));
            }
            if (response.getCreatedAt() != null && !response.getCreatedAt().isEmpty()) {
                result.setCreatedAt(LocalDateTime.parse(response.getCreatedAt()));
            }
            if (response.getUpdatedAt() != null && !response.getUpdatedAt().isEmpty()) {
                result.setUpdatedAt(LocalDateTime.parse(response.getUpdatedAt()));
            }

            log.info("Successfully fetched test order for barcode: {}", barcode);
            return result;

        } catch (io.grpc.StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.NOT_FOUND.getCode()) {
                log.info("Test order not found for barcode: {} (gRPC NOT_FOUND). Will create new.", barcode);
                return null;
            }
            log.error("gRPC error fetching test order for barcode: {}", barcode, e);
            throw new RuntimeException(
                    "Failed to fetch test order from Test Order Service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error communicating with Test Order Service for barcode: {}", barcode, e);
            throw new RuntimeException(
                    "Failed to fetch test order from Test Order Service: " + e.getMessage(), e);
        }
    }

    public CreationTestOrderResponse createUnmatchedOrder(String barcode) {
        try {
            log.info("Attempting to create unmatched test order via gRPC for barcode: {}", barcode);

            CreateUnmatchedOrderRequest request = CreateUnmatchedOrderRequest.newBuilder()
                    .setBarcode(barcode)
                    .build();

            CreateUnmatchedOrderResponse response = testOrderServiceStub.createUnmatchedOrder(request);

            if (!response.getSuccess()) {
                log.warn("Test Order Service failed to create order for barcode: {}", barcode);
                throw new RuntimeException("Test Order Service failed to create unmatched order");
            }


            CreationTestOrderResponse result = new CreationTestOrderResponse();
            result.setId(response.getId());
            result.setBarcode(response.getBarcode());
            result.setStatus(response.getStatus());
            result.setSuccess(response.getSuccess());
            result.setMessage(response.getMessage());
            if (response.getCreatedAt() != null && !response.getCreatedAt().isEmpty()) {
                result.setCreatedAt(LocalDateTime.parse(response.getCreatedAt()));
            }

            log.info("Successfully created unmatched test order for barcode: {}", barcode);
            return result;

        } catch (StatusRuntimeException e) {
            log.error("gRPC error creating unmatched order for barcode: {}", barcode, e);
            throw new RuntimeException(
                    "Failed to create unmatched test order: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating unmatched order via Test Order Service for barcode: {}", barcode, e);
            throw new RuntimeException(
                    "Failed to create unmatched test order: " + e.getMessage(), e);
        }
    }
}
