package sum25.group03.payment_service.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;
import sum25.group03.payment_service.services.interfaces.PaymentCacheService;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCacheServiceImpl implements PaymentCacheService {

    private final StringRedisTemplate redis;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${payment.redis.ttl:1800}")
    private long cacheTtl;

    @Override
    public void cachePaymentRequest(String key, PaymentRequestRequest request) {
        try {
            redisTemplate.opsForValue().set(key, request, cacheTtl, TimeUnit.SECONDS);
            log.info("[Redis] Cached payment request with key='{}' for {} minutes", key, cacheTtl);
        } catch (Exception e) {
            log.error("[Redis] Failed to cache payment request key='{}': {}", key, e.getMessage(), e);
        }
    }

    @Override
    public PaymentRequestRequest getCachedPaymentRequest(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof PaymentRequestRequest request) {
                log.info("[Redis] Retrieved cached payment request key='{}'", key);
                return request;
            } else if (cached != null) {
                log.warn("[Redis] Unexpected object type for key='{}': {}", key, cached.getClass().getName());
            }
        } catch (Exception e) {
            log.error("[Redis] Error retrieving cache key='{}': {}", key, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void removeCachedPaymentRequest(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            if (deleted) {
                log.info("[Redis] Removed cached payment request key='{}'", key);
            } else {
                log.warn("[Redis] Cache key='{}' not found or already removed", key);
            }
        } catch (Exception e) {
            log.error("[Redis] Failed to remove cache key='{}': {}", key, e.getMessage(), e);
        }
    }

    public void cacheTokenOrderCode(String token, String orderCode) {
        try {
            redisTemplate.opsForValue().set(token, orderCode, cacheTtl, TimeUnit.SECONDS);
            log.info("[Redis] Cached token='{}' with orderCode='{}'", token, orderCode);
        } catch (Exception e) {
            log.error("[Redis] Failed to cache token='{}': {}", token, e.getMessage(), e);
        }
    }

    public String getOrderCodeByToken(String token) {
        try {
            Object cached = redisTemplate.opsForValue().get(token);
            if (cached instanceof String orderCode) {
                log.info("[Redis] Retrieved orderCode='{}' for token='{}'", orderCode, token);
                return orderCode;
            } else if (cached != null) {
                log.warn("[Redis] Unexpected object type for token='{}': {}", token, cached.getClass().getName());
            }
        } catch (Exception e) {
            log.error("[Redis] Error retrieving token='{}': {}", token, e.getMessage(), e);
        }
        return null;
    }

    public void removeTokenOrderCode(String token) {
        try {
            Boolean deleted = redisTemplate.delete(token);
            if (deleted) {
                log.info("[Redis] Removed token='{}' from cache", token);
            } else {
                log.warn("[Redis] Token='{}' not found or already removed", token);
            }
        } catch (Exception e) {
            log.error("[Redis] Failed to remove token='{}': {}", token, e.getMessage(), e);
        }
    }

    private String key(String txnRef) {
        return "payment:status:" + txnRef;
    }

    @Override
    public void putStatus(String txnRef, String status, Duration ttl) {
        redis.opsForValue().set(key(txnRef), status, ttl);
    }

    @Override
    public Optional<String> getStatus(String txnRef) {
        return Optional.ofNullable(redis.opsForValue().get(key(txnRef)));
    }

    @Override
    public void delete(String txnRef) {
        redis.delete(key(txnRef));
    }
}
