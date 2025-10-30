package sum25.group03.payment_service.services.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.payment_service.services.interfaces.PaymentCacheService;

import java.time.Duration;
import java.util.Optional;

@Service
public class PaymentCacheServiceImpl implements PaymentCacheService {
    private final StringRedisTemplate redis;

    public PaymentCacheServiceImpl(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private String key(String txnRef) { return "payment:status:" + txnRef; }

    @Override
    public void putStatus(String txnRef, String status, Duration ttl) {
        redis.opsForValue().set(key(txnRef), status, ttl);
    }

    @Override
    public Optional<String> getStatus(String txnRef) {
        return Optional.ofNullable(redis.opsForValue().get(key(txnRef)));
    }

    @Override
    public void delete(String txnRef) { redis.delete(key(txnRef)); }
}
