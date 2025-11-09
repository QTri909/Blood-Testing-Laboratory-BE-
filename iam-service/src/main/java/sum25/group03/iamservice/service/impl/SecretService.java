package sum25.group03.iamservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import sum25.group03.iamservice.dto.CognitoConfig;

@Service
public class SecretService {

    private final SecretsManagerClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecretService() {
        this.client = SecretsManagerClient.builder()
                .region(Region.of("ap-southeast-2")) // region của secret
                .build();
    }

    public CognitoConfig getCognitoConfig(String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);
        String secretString = response.secretString();

        try {
            // Parse JSON thành object CognitoConfig
            return objectMapper.readValue(secretString, CognitoConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse secret JSON", e);
        }
    }

}
