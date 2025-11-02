package sum25.group03.payment_service.services.interfaces;

import java.time.Duration;
import java.util.Optional;

public interface PaymentCacheService {
    void putStatus(String txnRef, String status, Duration ttl);
    Optional<String> getStatus(String txnRef);
    void delete(String txnRef);
}
