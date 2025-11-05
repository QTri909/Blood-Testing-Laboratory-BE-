package sum25.group03.iamservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import sum25.group03.iamservice.event.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return props;
    }

    @Bean
    public ProducerFactory<String, UserCreatedEvent> userCreatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, UserUpdatedEvent> userUpdatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, UserDeletedEvent> userDeletedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, RoleCreatedEvent> roleCreatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, RoleUpdatedEvent> roleUpdatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, RoleDeletedEvent> roleDeletedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, PasswordChangedEvent> passwordChangedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, UserCreatedEvent> userCreatedKafkaTemplate() {
        return new KafkaTemplate<>(userCreatedProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, UserUpdatedEvent> userUpdatedKafkaTemplate() {
        return new KafkaTemplate<>(userUpdatedProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, UserDeletedEvent> userDeletedKafkaTemplate() {
        return new KafkaTemplate<>(userDeletedProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, RoleCreatedEvent> roleCreatedKafkaTemplate() {
        return new KafkaTemplate<>(roleCreatedProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, RoleUpdatedEvent> roleUpdatedKafkaTemplate() {
        return new KafkaTemplate<>(roleUpdatedProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, RoleDeletedEvent> roleDeletedKafkaTemplate() {
        return new KafkaTemplate<>(roleDeletedProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, PasswordChangedEvent> passwordChangedKafkaTemplate() {
        return new KafkaTemplate<>(passwordChangedProducerFactory());
    }

//    @Bean
//    public ConsumerFactory<String, ExternalEvent> externalEventConsumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "iam-service-group");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//        return new DefaultKafkaConsumerFactory<>(
//                props,
//                new StringDeserializer(),
//                new JsonDeserializer<>(ExternalEvent.class, false)
//        );
//    }
//
//    @Bean(name = "externalEventListenerContainerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, ExternalEvent> externalEventListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, ExternalEvent> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(externalEventConsumerFactory());
//        return factory;
//    }
}
