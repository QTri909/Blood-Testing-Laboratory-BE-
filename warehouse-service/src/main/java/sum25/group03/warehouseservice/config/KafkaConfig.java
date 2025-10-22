package sum25.group03.warehouseservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import sum25.group03.warehouseservice.event.InstrumentModeChangedEvent;
import sum25.group03.warehouseservice.event.ReagentInstalledEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:warehouse-service-group}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, ReagentInstalledEvent> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ReagentInstalledEvent.class.getName());
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConsumerFactory<String, InstrumentModeChangedEvent> instrumentModeConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, InstrumentModeChangedEvent.class.getName());
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            System.err.println("[Kafka Error] Failed to deserialize message from topic: " + record.topic() +
                    ", partition: " + record.partition() + ", offset: " + record.offset());
            System.err.println("[Kafka Error] Exception: " + exception.getMessage());
        }, new FixedBackOff(1000, 3));

        errorHandler.addNotRetryableExceptions(org.springframework.messaging.converter.MessageConversionException.class);

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReagentInstalledEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReagentInstalledEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler());

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InstrumentModeChangedEvent> instrumentModeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InstrumentModeChangedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(instrumentModeConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());

        return factory;
    }
}
