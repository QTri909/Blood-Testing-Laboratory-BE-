package sum25.group03.iamservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import sum25.group03.iamservice.service.impl.SecretService;

@Configuration
@RequiredArgsConstructor
public class AwsCognitoConfig {

    private final SecretService secretService;

    @Bean
    public CognitoIdentityProviderClient cognitoClient() {
        SecretService.AwsCredentials awsCreds = secretService.getAwsCredentials("IAMService/AwsCredentials");

        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        awsCreds.getAccessKeyId(),
                                        awsCreds.getSecretAccessKey()
                                )
                        )
                )
                .region(Region.of(awsCreds.getRegion()))
                .build();
    }
}
