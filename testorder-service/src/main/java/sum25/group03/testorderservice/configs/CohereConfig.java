package sum25.group03.testorderservice.configs;


import com.cohere.api.Cohere;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohereConfig {

    @Value("${corehere.api-key}")
    private String apiKey;

    @Value("${corehere.client-name}")
    private String clientName;

    @Bean
    public Cohere cohereClient() {
        return Cohere.builder()
                .token(apiKey)
                .clientName(clientName)
                .build();
    }
}

