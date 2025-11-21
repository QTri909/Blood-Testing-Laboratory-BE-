package sum25.group03.payment_service.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import sum25.group03.common.response.constants.KafkaConstants;

@Configuration
@EnableKafka
public class PaymentKafkaConfig {

    @Bean
    public NewTopic paymentResultTopic() {
        return TopicBuilder.name(KafkaConstants.PAYMENT_RESULT_TOPIC)
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(15 * 60 * 1000L)) // 15 mins
                .build();
    }
}
