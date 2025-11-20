package sum25.group03.payment_service.services.interfaces;

import org.springframework.data.domain.Page;
import sum25.group03.payment_service.dtos.request.RequestTransactionsByRequestId;
import sum25.group03.payment_service.dtos.response.PaymentTransactionRes;

public interface PaymentTransactionService {
    void captureAndUpdateStatus(String oderCode);
    void handlePaymentFailure(String oderCode, String reason);

    Page<PaymentTransactionRes> getAllTransactionsByPaymentRequestId(RequestTransactionsByRequestId request);
}
