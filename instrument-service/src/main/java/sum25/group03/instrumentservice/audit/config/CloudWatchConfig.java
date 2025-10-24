package sum25.group03.instrumentservice.audit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;


@Configuration
public class CloudWatchConfig {
    @Value("${aws.region:ap-southeast-1}")
    private String awsRegion;

    @Value("${aws.access-key-id}")
    private String awsAccessKeyId;

    @Value("${aws.secret-key-secret}")
    private String awsSecretKeySecret;

    @Bean
    public CloudWatchLogsClient cloudWatchLogsClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                awsAccessKeyId,
                awsSecretKeySecret
        );

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        return CloudWatchLogsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
