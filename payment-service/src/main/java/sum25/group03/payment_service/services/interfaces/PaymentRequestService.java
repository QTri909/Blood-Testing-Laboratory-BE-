package sum25.group03.payment_service.services.interfaces;

import org.springframework.data.domain.Page;
import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;

import java.util.List;

public interface PaymentRequestService {

    PaymentRequestResponse createPaymentRequest(PaymentRequestRequest request);

    PaymentRequestResponse getByOrderCode(String orderCode);

    List<PaymentRequestResponse> getAllByUserId(Long userId);
    Page<PaymentRequestResponse> getAllPaymentRequests(Integer page, Integer size, Long viewerId);

    void updateStatus(String orderCode, String status);

    void deletePendingPayment(String orderCode);

}
