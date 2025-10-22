package sum25.group03.instrumentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sum25.group03.instrumentservice.exception.WarehouseServiceException;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceClient {
    private final RestTemplate restTemplate;

    @Value("${warehouse.service.url:http://localhost:8082}")
    private String warehouseServiceUrl;


    public boolean checkInstrumentStatus(Integer instrumentId) {
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


    public Map<String, Object> getInstrumentDetails(Integer instrumentId) {
        try {
            String url = warehouseServiceUrl + "/api/v1/instruments/" + instrumentId;
            log.info("[v0] Fetching instrument details from Warehouse Service: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                log.warn("[v0] Warehouse Service returned null response for instrument details: {}", instrumentId);
                throw new WarehouseServiceException("Warehouse Service returned null response");
            }

            log.info("[v0] Successfully retrieved instrument details for ID: {}", instrumentId);
            return response;

        } catch (RestClientException e) {
            log.error("[v0] Error fetching instrument details from Warehouse Service for ID: {}", instrumentId, e);
            throw new WarehouseServiceException(
                    "Failed to fetch instrument details from Warehouse Service: " + e.getMessage(), e);
        }
    }
}
