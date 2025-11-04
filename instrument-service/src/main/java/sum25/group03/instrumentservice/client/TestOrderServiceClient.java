package sum25.group03.instrumentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sum25.group03.instrumentservice.client.response.CreationTestOrderResponse;
import sum25.group03.instrumentservice.client.response.TestOrderResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestOrderServiceClient {
    private final RestTemplate restTemplate;

    @Value("${test-order.service.url:http://localhost:8081}")
    private String testOrderServiceUrl;

    public TestOrderResponse getTestOrderByBarcode(String barcode) {
        String url = testOrderServiceUrl + "/api/v1/test-orders/by-barcode/" + barcode;
        log.info("Fetching test order by barcode from Test Order Service: {}", url);

        try {
            TestOrderResponse response = restTemplate.getForObject(url, TestOrderResponse.class);

            if (response == null) {
                log.warn("Test Order Service returned null response for barcode: {}", barcode);
                return null;
            }

            log.info("Successfully fetched test order for barcode: {}", barcode);
            return response;

        } catch (HttpClientErrorException.NotFound e) {
            log.info("Test order not found for barcode: {} (HTTP 404). Sẽ tạo mới.", barcode);
            return null;

        } catch (Exception e) {
            log.error("Error communicating with Test Order Service for barcode: {}", barcode, e);
            throw new RuntimeException(
                    "Failed to fetch test order from Test Order Service: " + e.getMessage(), e);
        }
    }
    public CreationTestOrderResponse createUnmatchedOrder (String barcode) {
        String baseUrl = testOrderServiceUrl + "/api/v1/test-orders/create-unmatched-order";

        log.info("Attempting to create unmatched test order for barcode: {}", barcode);


        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("barcode", barcode)
                .toUriString();

        log.info("Posting to Test Order Service: {}", url);

        try {
            CreationTestOrderResponse response = restTemplate.postForObject(
                    url,
                    null,
                    CreationTestOrderResponse.class
            );

            if (response == null) {
                log.warn("Test Order Service returned null response for creation request, barcode: {}", barcode);
                throw new RuntimeException("Test Order Service returned null response");
            }

            log.info("Successfully created unmatched test order for barcode: {}", barcode);
            return response;

        } catch (Exception e) {
            log.error("Error creating unmatched order via Test Order Service for barcode: {}", barcode, e);
            throw new RuntimeException(
                    "Failed to create unmatched test order: " + e.getMessage(), e);
        }
    }
}
