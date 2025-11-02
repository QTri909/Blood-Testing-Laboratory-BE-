package sum25.group03.payment_service.services.impl;

import com.google.gson.reflect.TypeToken;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sum25.group03.payment_service.entities.PaymentProvider;
import sum25.group03.payment_service.entities.PaymentRequest;
import sum25.group03.payment_service.entities.PaymentTransaction;
import sum25.group03.payment_service.enums.*;
import sum25.group03.payment_service.repostitories.PaymentProviderRepository;
import sum25.group03.payment_service.repostitories.PaymentRequestRepository;
import sum25.group03.payment_service.repostitories.PaymentTransactionRepository;
import sum25.group03.payment_service.services.interfaces.IStripeService;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class StripeServiceImpl implements IStripeService {

    private PaymentProviderRepository paymentProviderRepository;
    private PaymentTransactionRepository paymentTransactionRepository;
    private PaymentRequestRepository paymentRequestRepository;

    public StripeServiceImpl(PaymentProviderRepository paymentProviderRepository,
                             PaymentRequestRepository paymentRequestRepository,
                             PaymentTransactionRepository paymentTransactionRepository)
    {
        this.paymentProviderRepository = paymentProviderRepository;
        this.paymentRequestRepository = paymentRequestRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;

    }

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.web-hook-secret}")
    private String webhookSecret;

    @Transactional
    public Map<String, Object> createPaymentIntent(Long amount, String currency) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        PaymentProvider paymentProvider = paymentProviderRepository.findById(String.valueOf(1))
                .orElseThrow(EntityNotFoundException::new);
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("automatic_payment_methods", Map.of("enabled", true));

        String orderCode = "1"; // TODO: This should be 'real' test order from order service, datatype must be UUID
        Long userId = 1L;       // TODO: This should be 'real' test user from iam service/test order service
        StandardCurrency standardCurrency = StandardCurrency.valueOf(currency.toUpperCase());
        PaymentRequest paymentRequest = new PaymentRequest(orderCode, userId, Double.valueOf(amount), standardCurrency , PaymentRequestStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), paymentProvider);
        paymentRequestRepository.saveAndFlush(paymentRequest);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("payment_request_id", paymentRequest.getId()); // TODO: payment request id should be UUID, convert it to string then cast back later
        params.put("metadata", metadata);

        log.info("Creating PaymentIntent with amount: {} {}", amount, currency);

        PaymentIntent intent = PaymentIntent.create(params); // create http request to stripe server

        return Map.of("clientSecret", intent.getClientSecret());
    }

    public Event verifySignature(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            log.error("Invalid signature: {}", e.getMessage());
            throw new RuntimeException("Invalid signature");
        }
    }

    public void processEvent(Event event) {
        log.info("Received event type: {}", event.getType());
        Map<String, Object> rawResponse;
        try {
            String rawJson = event.getDataObjectDeserializer().getRawJson();
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            rawResponse = ApiResource.GSON.fromJson(rawJson, type);
        } catch (Exception e) {
            log.error("Failed to parse PaymentIntent: {}", e.getMessage());
            return;
        }
        switch (event.getType()) {
            case "payment_intent.succeeded": // TODO: use final static strings instead of hardcoded strings
                handlePaymentSucceeded(rawResponse);
                break;
            case "payment_intent.payment_failed":
                handlePaymentFailed(rawResponse);
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }
    }

    public void handlePaymentSucceeded(Map<String, Object> rawResponse) {
        PaymentRequest paymentRequest = getPaymentRequest(rawResponse);
        PaymentTransaction paymentTransaction = new PaymentTransaction( paymentRequest, "Stripe", PaymentTransactionStatus.SUCCESS, rawResponse , LocalDateTime.now(), LocalDateTime.now());
        paymentTransactionRepository.save(paymentTransaction);
        paymentRequest.setStatus(PaymentRequestStatus.SUCCESS);
        paymentRequestRepository.save(paymentRequest);
    }

    public void handlePaymentFailed(Map<String, Object> rawResponse) {
        PaymentRequest paymentRequest = getPaymentRequest(rawResponse);
        PaymentTransaction paymentTransaction = new PaymentTransaction(paymentRequest, "Stripe", PaymentTransactionStatus.FAILED, rawResponse , LocalDateTime.now(), LocalDateTime.now());
        paymentTransactionRepository.save(paymentTransaction);
        paymentRequest.setStatus(PaymentRequestStatus.FAILED);
        paymentRequestRepository.save(paymentRequest);
    }

    public PaymentRequest getPaymentRequest(Map<String, Object> rawResponse) {
        Map<String, Object> metadata = (Map<String, Object>) rawResponse.get("metadata");
        if (metadata == null) {
            throw new EntityNotFoundException("Metadata is missing");
        }
        String paymentRequestId = (String) metadata.get("payment_request_id"); // TODO: change to UUID if possible
        if (paymentRequestId == null) {
            throw new EntityNotFoundException("payment_request_id is missing in metadata");
        }

        Optional<PaymentRequest> paymentRequestOpt = paymentRequestRepository.findById(paymentRequestId);
        return paymentRequestOpt.orElseThrow(() -> new EntityNotFoundException("Payment request not found"));
    }
}
