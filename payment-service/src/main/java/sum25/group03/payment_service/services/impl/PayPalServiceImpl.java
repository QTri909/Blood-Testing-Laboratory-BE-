package sum25.group03.payment_service.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;
import sum25.group03.payment_service.services.interfaces.PayPalService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalServiceImpl implements PayPalService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${paypal.api.base-url:https://api-m.sandbox.paypal.com}")
    private String baseUrl;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    /**
     * Get PayPal access token using client credentials.
     */
    private String getAccessToken() {
        try {
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/v1/oauth2/token",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("access_token").asText();

        } catch (Exception e) {
            log.error("Error getting PayPal access token", e);
            throw new RuntimeException("Failed to get PayPal token");
        }
    }

    /**
     * Create PayPal payment order.
     */
    @Override
    public String createPayment(PaymentRequestRequest request) {
        try {
            String token = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = """
                {
                  "intent": "CAPTURE",
                  "purchase_units": [{
                    "amount": {
                      "currency_code": "%s",
                      "value": "%.2f"
                    }
                  }],
                  "application_context": {
                    "return_url": "%s",
                    "cancel_url": "%s"
                  }
                }
            """.formatted(
                    request.getCurrency().name(),
                    request.getAmount(),
                    "https://4341644fec05.ngrok-free.app/api/paypal/return",
                    "https://4341644fec05.ngrok-free.app/api/paypal/cancel"
            );

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/v2/checkout/orders",
                    entity,
                    String.class
            );

            JsonNode json = objectMapper.readTree(response.getBody());
            String approveUrl = null;
            for (JsonNode link : json.get("links")) {
                if ("approve".equals(link.get("rel").asText())) {
                    approveUrl = link.get("href").asText();
                    break;
                }
            }

            if (approveUrl == null) {
                throw new RuntimeException("Approval URL not found in PayPal response");
            }

            log.info("PayPal order created successfully: {}", json.get("id").asText());
            return approveUrl;

        } catch (Exception e) {
            log.error("Error creating PayPal payment", e);
            throw new RuntimeException("Failed to create PayPal payment");
        }
    }

    /**
     * Capture payment for given PayPal order ID.
     */
    @Override
    public String capturePayment(String orderId) {
        try {
            String token = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/v2/checkout/orders/" + orderId + "/capture",
                    new HttpEntity<>("", headers),
                    String.class
            );

            log.info("PayPal order captured successfully: {}", orderId);
            return response.getBody();

        } catch (Exception e) {
            log.error("Error capturing PayPal payment", e);
            throw new RuntimeException("Failed to capture PayPal payment");
        }
    }

    /**
     * Retrieve PayPal order details.
     */
    @Override
    public String getPaymentDetails(String orderId) {
        try {
            String token = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/v2/checkout/orders/" + orderId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error getting PayPal payment details", e);
            throw new RuntimeException("Failed to get PayPal payment details");
        }
    }

    @Override
    public String getOrderStatus(String orderId) {
        try {
            String details = getPaymentDetails(orderId);
            JsonNode json = objectMapper.readTree(details);
            String status = json.get("status").asText();
            log.info("ℹ️ PayPal order status: id={}, status={}", orderId, status);
            return status;
        } catch (Exception e) {
            log.error("Error getting PayPal order status", e);
            throw new RuntimeException("Failed to get PayPal order status");
        }
    }
}
