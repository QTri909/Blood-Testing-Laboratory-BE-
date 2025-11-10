package sum25.group03.payment_service.services.interfaces;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import sum25.group03.payment_service.entities.PaymentRequest;

import java.util.Map;

public interface IStripeService {
    public Map<String, Object> createPaymentIntent(Long amount, String currency) throws StripeException;
    public Event verifySignature(String payload, String sigHeader);
    public void processEvent(Event event);
    public void handlePaymentSucceeded(Map<String, Object> rawResponse);
    public void handlePaymentFailed(Map<String, Object> rawResponse);
    public PaymentRequest getPaymentRequest(Map<String, Object> rawResponse);
}
