package sum25.group03.iamservice.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
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
        // 1️ Tạo client tạm thời dùng DefaultCredentialsProvider
        //    SDK sẽ tự tìm từ: env var, profile, IAM Role (EC2/ECS)
        SecretsManagerClient tempClient = SecretsManagerClient.builder()
                .region(Region.of("ap-southeast-2"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        // 2️ Lấy AWS credentials thật từ secret IAMService/AwsCredentials
        AwsCredentials awsCreds = fetchAwsCredentials(tempClient, "IAMService/AwsCredentials");

        // 3️ Tạo client chính thức với credentials này
        AwsBasicCredentials basicCreds = AwsBasicCredentials.create(
                awsCreds.getAccessKeyId(),
                awsCreds.getSecretAccessKey()
        );

        this.client = SecretsManagerClient.builder()
                .region(Region.of(awsCreds.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(basicCreds))
                .build();
    }


    public CognitoConfig getCognitoConfig(String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);
        try {
            return objectMapper.readValue(response.secretString(), CognitoConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse secret JSON for " + secretName, e);
        }
    }

    public AwsCredentials getAwsCredentials(String secretName) {
        return fetchAwsCredentials(client, secretName);
    }

    private AwsCredentials fetchAwsCredentials(SecretsManagerClient client, String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);

        try {
            return objectMapper.readValue(response.secretString(), AwsCredentials.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AWS credentials from secret: " + secretName, e);
        }
    }

    @Data
    public static class AwsCredentials {
        @JsonProperty("AWS_ACCESS_KEY_ID")
        private String accessKeyId;

        @JsonProperty("AWS_SECRET_ACCESS_KEY")
        private String secretAccessKey;

        @JsonProperty("AWS_REGION")
        private String region;
    }
}
