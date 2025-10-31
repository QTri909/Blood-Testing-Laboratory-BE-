package sum25.group03.payment_service.services.interfaces;

import sum25.group03.payment_service.dtos.request.PaymentProviderRequest;
import sum25.group03.payment_service.dtos.response.PaymentProviderResponse;

import java.util.List;

public interface PaymentProviderService {
    PaymentProviderResponse create(PaymentProviderRequest request);
    PaymentProviderResponse update(String id, PaymentProviderRequest request);
    void delete(String id);
    PaymentProviderResponse getById(String id);
    List<PaymentProviderResponse> getAll();
}