package sum25.group03.iamservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.iamservice.service.Interface.RedisService;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public void saveValue(String key, String value, long expirationInSeconds) {
        // Save the value with an expiration time
        stringRedisTemplate.opsForValue().set(key, value);
        stringRedisTemplate.expire(key, java.time.Duration.ofSeconds(expirationInSeconds));
    }

    @Override
    public String getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteValue(String key) {
        stringRedisTemplate.delete(key);
    }
}
