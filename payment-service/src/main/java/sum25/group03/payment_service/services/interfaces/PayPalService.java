package sum25.group03.payment_service.services.interfaces;

import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;
import sum25.group03.payment_service.dtos.request.PaymentTransactionRequest;

public interface PayPalService {
    String createPayment(PaymentRequestRequest request);
    String capturePayment(String orderId);
    String getPaymentDetails(String orderId);
    String getOrderStatus(String orderId);
}
