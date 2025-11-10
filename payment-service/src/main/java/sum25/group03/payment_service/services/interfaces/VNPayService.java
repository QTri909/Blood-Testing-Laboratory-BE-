package sum25.group03.payment_service.services.interfaces;

import sum25.group03.payment_service.dtos.request.VNPayCreatePaymentRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;
import sum25.group03.payment_service.dtos.response.PaymentResponseDTO;
import sum25.group03.payment_service.dtos.response.VNPayCreatePaymentResponse;

import java.util.Map;

public interface VNPayService {
    VNPayCreatePaymentResponse create(VNPayCreatePaymentRequest req, String clientIp);
    Map<String, String> handleIpn(Map<String, String> params);
    PaymentResponseDTO handleReturn(Map<String, String> params);
    PaymentResponseDTO queryByTxnRef(String txnRef);
    PaymentRequestResponse queryRequestByTxnRef(String txnRef);
}
