package sum25.group03.testorderservice.configs;


import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import sum25.group03.testorderservice.constants.KafkaVariables;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // TOPIC
    @Bean
    public NewTopic testOrderTopic() {
        return TopicBuilder.name(KafkaVariables.TEST_ORDER_TOPIC)
                .partitions(3)
                .configs(Map.of(KafkaVariables.RETENTION_MS, String.valueOf(7 * 24 * 60 * 60 * 1000L))) // keep at least 7 days of data
                .replicas(1)
                .build();
    }

    //--------------------------


    @Bean
    public NewTopic hl7Topic() {
        return TopicBuilder.name("test-order-result")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic quarantineTopic() {
        return TopicBuilder.name("test-order-quarantine")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> objectProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    @Qualifier("objectKafkaTemplate")
    public KafkaTemplate<String, Object> objectKafkaTemplate() {
        return new KafkaTemplate<>(objectProducerFactory());
    }
}
