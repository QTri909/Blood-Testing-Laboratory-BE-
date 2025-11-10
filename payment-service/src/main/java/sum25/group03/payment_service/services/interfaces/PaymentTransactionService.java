package sum25.group03.payment_service.services.interfaces;

public interface PaymentTransactionService {
    void captureAndUpdateStatus(String oderCode);
    void handlePaymentFailure(String oderCode, String reason);
}
