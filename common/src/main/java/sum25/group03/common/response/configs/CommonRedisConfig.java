package sum25.group03.common.response.configs;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class CommonRedisConfig {

    // Give a clear bean name and use it in the template
    @Bean(name = "commonRedisConnectionFactory")
    @Primary
    public LettuceConnectionFactory commonRedisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName("redis-14412.crce194.ap-seast-1-1.ec2.cloud.redislabs.com");
        standaloneConfig.setPort(14412);
        standaloneConfig.setPassword("cpArwsG5k0E6Ci7Rn0ilcbPUuPIF755b");
        standaloneConfig.setDatabase(0);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(5000))
                .shutdownTimeout(Duration.ofMillis(100))
                .clientOptions(ClientOptions.builder()
                        .timeoutOptions(TimeoutOptions.enabled(Duration.ofMillis(5000)))
                        .build())
                .build();

        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }

    // Use @Qualifier to explicitly pick the common factory
    @Bean(name = "commonStringRedisTemplate")
    public RedisTemplate<String, String> commonStringRedisTemplate(
            @Qualifier("commonRedisConnectionFactory") RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

}