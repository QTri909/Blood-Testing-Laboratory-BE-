package sum25.group03.payment_service.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.payment_service.services.impl.StripeServiceImpl;

import java.util.Map;
import java.util.logging.Logger;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/payments/stripe")
@Slf4j
public class StripeController {

    private final StripeServiceImpl stripeService;
    private static final Logger logger = Logger.getLogger(StripeController.class.getName());

    public StripeController(StripeServiceImpl stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> data) throws Exception {
        try {
            long amount = Long.parseLong(data.get("amount").toString());
            String currency = data.get("currency").toString().toLowerCase();

            Map<String, Object> response = stripeService.createPaymentIntent(amount, currency);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.severe("Stripe error: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.severe("Internal error: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", "Invalid request data"));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = stripeService.verifySignature(payload, sigHeader);
            stripeService.processEvent(event);
            return ResponseEntity.ok("Event processed");
        } catch (Exception e) {
            log.error("Webhook error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }
    }
}