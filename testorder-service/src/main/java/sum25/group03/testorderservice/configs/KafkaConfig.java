package sum25.group03.testorderservice.configs;


import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import sum25.group03.common.response.events.MonitoringLogEvent;
import sum25.group03.testorderservice.constants.KafkaVariables;
import sum25.group03.testorderservice.dtos.request.TestResultPublishedEventDTO;

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
    public ProducerFactory<String, Object> objectProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, MonitoringLogEvent> monitoringLogEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    @Qualifier("objectKafkaTemplate")
    public KafkaTemplate<String, Object> objectKafkaTemplate() {
        return new KafkaTemplate<>(objectProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, MonitoringLogEvent> monitoringLogEventKafkaTemplate() {
        return new KafkaTemplate<>(monitoringLogEventProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, TestResultPublishedEventDTO> testResultConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-result-group");

        // Key deserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Value deserializer với ErrorHandling để không crash khi deserialize lỗi
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Trust tất cả packages vì class gốc ở service khác
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // Mapping class sang DTO của chúng ta
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TestResultPublishedEventDTO.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TestResultPublishedEventDTO> testResultKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TestResultPublishedEventDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(testResultConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
