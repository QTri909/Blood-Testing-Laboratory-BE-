package sum25.group03.instrumentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sum25.group03.instrumentservice.client.response.ReagentResponse;
import sum25.group03.instrumentservice.client.response.ReagentValidationResponse;
import sum25.group03.instrumentservice.exception.WarehouseServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceClient {
    private final RestTemplate restTemplate;

    @Value("${warehouse.service.url:http://localhost:8082}")
    private String warehouseServiceUrl;


    public boolean checkInstrumentStatus(Long instrumentId) {
        try {
            String url = warehouseServiceUrl + "/api/v1/instruments/status/" + instrumentId;
            log.info("Checking instrument status from Warehouse Service: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                log.warn("Warehouse Service returned null response for instrument ID: {}", instrumentId);
                throw new WarehouseServiceException("Warehouse Service returned null response");
            }

            Object statusObj = response.get("status");
            if (statusObj == null) {
                log.warn("No status field in Warehouse Service response for instrument ID: {}", instrumentId);
                throw new WarehouseServiceException("Invalid response format from Warehouse Service");
            }

            String status = statusObj.toString().toUpperCase();
            boolean isActive = "ACTIVE".equals(status);

            log.info("Instrument {} status from Warehouse: {} (Active: {})", instrumentId, status, isActive);
            return isActive;

        } catch (RestClientException e) {
            log.error("Error communicating with Warehouse Service for instrument ID: {}", instrumentId, e);
            throw new WarehouseServiceException(
                    "Failed to check instrument status from Warehouse Service: " + e.getMessage(), e);
        }
    }


    public ReagentValidationResponse validateReagent(String lotNumber, Double requiredVolume) {


        String urlTemplate = warehouseServiceUrl + "/api/v1/reagents/validate/{lotNumber}";

        String url = UriComponentsBuilder.fromUriString(urlTemplate)
                .queryParam("requiredVolume", requiredVolume)
                .buildAndExpand(lotNumber)
                .toUriString();

        try {
            log.info("Validating reagent from Warehouse Service: {}", url);

            ReagentValidationResponse response = restTemplate.getForObject(url, ReagentValidationResponse.class);

            if (response == null) {
                log.warn("Warehouse Service returned null response for reagent batch: {}", lotNumber);
                throw new WarehouseServiceException("Warehouse Service returned null response");
            }

            log.info("Reagent validation result - Valid: {}, Message: {}", response.isValid(), response.getMessage());
            return response;

        } catch (RestClientException e) {
            log.error("Error validating reagent from Warehouse Service for batch: {}", lotNumber, e);
            throw new WarehouseServiceException(
                    "Failed to validate reagent from Warehouse Service: " + e.getMessage(), e);
        }
    }

    public List<ReagentResponse> reagentResponseReagentList() {
        String url = warehouseServiceUrl + "/api/v1/reagents/list";

        try {
            log.info("Listing reagents for instrument from Warehouse Service: {}", url);

            ReagentResponse[] responseArray = restTemplate.getForObject(url, ReagentResponse[].class);

            if (responseArray == null) {
                log.warn("Warehouse Service returned null response for reagent list");
                throw new WarehouseServiceException("Warehouse Service returned null response");
            }

            List<ReagentResponse> responseList = List.of(responseArray);

            log.info("Retrieved {} reagents for instrument from Warehouse Service", responseList.size());
            return responseList;

        } catch (RestClientException e) {
            log.error("Error listing reagents from Warehouse Service", e);
            throw new WarehouseServiceException(
                    "Failed to list reagents from Warehouse Service: " + e.getMessage(), e);
        }
    }
}
