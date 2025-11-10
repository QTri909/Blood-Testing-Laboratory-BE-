package sum25.group03.iamservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
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

    @Bean
    public KafkaAdmin kafkaAdmin(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name("iam.user.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userUpdatedTopic() {
        return TopicBuilder.name("iam.user.updated").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name("iam.user.deleted").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic roleCreatedTopic() {
        return TopicBuilder.name("iam.role.created").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic roleUpdatedTopic() {
        return TopicBuilder.name("iam.role.updated").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic roleDeletedTopic() {
        return TopicBuilder.name("iam.role.deleted").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic passwordChangedTopic() {
        return TopicBuilder.name("iam.password.changed").partitions(3).replicas(1).build();
    }

<<<<<<< HEAD


=======
>>>>>>> origin/Team07/iam-login
    @Bean
    public ConsumerFactory<String, UserCreatedEvent> userCreatedEventConsumerFactory() {
        JsonDeserializer<UserCreatedEvent> deserializer = new JsonDeserializer<>(UserCreatedEvent.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "iam-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> userCreatedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userCreatedEventConsumerFactory());
        return factory;
    }
}
