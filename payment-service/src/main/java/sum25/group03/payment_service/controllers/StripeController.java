package sum25.group03.payment_service.controllers;

import com.google.gson.reflect.TypeToken;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.ApiResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.payment_service.dtos.request.StripeConfirmRequest;
import sum25.group03.payment_service.services.impl.StripeServiceImpl;

import java.lang.reflect.Type;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/stripe")
@Slf4j
public class StripeController {
    @GetMapping("")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from StripeController");
    }
    private final StripeServiceImpl stripeService;

    public StripeController(StripeServiceImpl stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-payment-intent")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> createPaymentIntent(@RequestBody Map<String, Object> data) throws Exception {
        try {
            long amount = Long.parseLong(data.get("amount").toString());
            String currency = data.get("currency").toString().toLowerCase();

            Map<String, Object> response = stripeService.createPaymentIntent(amount, currency);
            return ApiResponse.add("Payment Intent created", response);
        } catch (StripeException e) {
            return ApiResponse.internalServerError("Stripe error: " + e.getMessage(), null);

        } catch (Exception e) {
            return ApiResponse.badRequest("Invalid request data: " + e.getMessage(), null);
        }
    }

    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = stripeService.verifySignature(payload, sigHeader);
            stripeService.processEvent(event);
            return ApiResponse.add("Event processed", null);
        } catch (Exception e) {
            return ApiResponse.badRequest("Webhook error: " + e.getMessage(), null);
        }
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> handleStripeConfirm(
            @RequestBody StripeConfirmRequest req
    ) throws StripeException {
        try {
            // 1️⃣ Lấy PaymentIntent từ Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(req.getPaymentIntentId());

            // 2️⃣ Lấy raw JSON từ PaymentIntent
            String rawJson = paymentIntent.toJson();

            // 3️⃣ Convert sang Map<String,Object> giống rawResponse
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> rawResponse = ApiResource.GSON.fromJson(rawJson, type);

            // 4️⃣ Gọi service cũ dựa vào trạng thái PaymentIntent
            String status = paymentIntent.getStatus();
            switch (status) {
                case "succeeded":
                    stripeService.handlePaymentSucceeded(rawResponse);
                    break;
                case "requires_payment_method":
                case "canceled":
                    stripeService.handlePaymentFailed(rawResponse);
                    break;
                default:
                    log.info("Unhandled PaymentIntent status: {}", status);
            }

            return ApiResponse.add("success", null);
        } catch (StripeException e) {
            log.error("Stripe API error: {}", e.getMessage());
            return ApiResponse.add("Stripe API error", null);
        } catch (Exception e) {
            log.error("Failed to process payment: {}", e.getMessage());
            return ApiResponse.add("Failed to process payment", null);
        }
    }
}