package sum25.group03.payment_service.services.interfaces;
import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface PaymentCacheService {
    void cachePaymentRequest(String key, PaymentRequestRequest request);
    PaymentRequestRequest getCachedPaymentRequest(String key);
    void removeCachedPaymentRequest(String key);

    void cacheTokenOrderCode(String token, String orderCode);
    String getOrderCodeByToken(String token);
    void removeTokenOrderCode(String token);

    void putStatus(String txnRef, String status, Duration ttl);
    Optional<String> getStatus(String txnRef);
    void delete(String txnRef);
    void addTokenForOrderCode(String orderCode, String token);
    List<String> getTokensForOrderCode(String orderCode);
    void removeAllTokensForOrderCode(String orderCode);

    void cacheTokenRequestId(String token, String requestId);
    String getRequestIdByToken(String token);
    void removeTokenRequestId(String token);
}
