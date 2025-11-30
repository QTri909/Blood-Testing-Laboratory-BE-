package sum25.group03.monitoringservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;
import sum25.group03.common.response.services.interfaces.CommonRedisService;

@Component
public class TestRedis implements CommandLineRunner {

    private final CommonRedisService commonRedisService;
    private final RedisConnectionFactory redisConnectionFactory;

    public TestRedis(CommonRedisService commonRedisService,
                     RedisConnectionFactory redisConnectionFactory) {
        this.commonRedisService = commonRedisService;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void run(String... args) throws Exception {
        String testKey = "test-key";
        String testValue = "test-value";
        int ttlSeconds = 120;

        System.out.println("==== Redis Connection Test Start ====");

        // Log host, port, DB
        if (redisConnectionFactory instanceof LettuceConnectionFactory lettuceFactory) {
            System.out.println("Redis host: " + lettuceFactory.getHostName());
            System.out.println("Redis port: " + lettuceFactory.getPort());
            System.out.println("Redis DB index: " + lettuceFactory.getDatabase());
        } else {
            System.out.println("RedisConnectionFactory class: " + redisConnectionFactory.getClass().getName());
        }

        try {
            // Test save
            commonRedisService.saveValue(testKey, testValue, ttlSeconds);
            System.out.println("Saved key='" + testKey + "' with value='" + testValue + "'");

            // Test read
            String value = commonRedisService.getValue(testKey);
            if (value != null) {
                System.out.println("Read key='" + testKey + "' successfully, value='" + value + "'");
            } else {
                System.out.println("Read key='" + testKey + "' returned null! Check Redis connection or DB index.");
            }
        } catch (Exception e) {
            System.err.println("Redis operation failed!");
            e.printStackTrace();
        }

        System.out.println("==== Redis Connection Test End ====");
    }
}
