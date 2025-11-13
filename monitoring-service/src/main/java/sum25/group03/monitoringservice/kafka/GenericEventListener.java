package sum25.group03.monitoringservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.config.MonitoringKafkaProperties;

@Service
@Slf4j
public class GenericEventListener {

    private final EventLogProcessor processor;
    private final MonitoringKafkaProperties kafkaProperties;

    @Autowired
    public GenericEventListener(EventLogProcessor processor,
                                MonitoringKafkaProperties kafkaProperties) {
        this.processor = processor;
        this.kafkaProperties = kafkaProperties;
    }

    @KafkaListener(
            topics = "#{__listener.getTopics()}",
            groupId = "monitoring-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.debug("Received message on topic {}: {}", record.topic(), record.value());
            processor.process(record);
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process message from topic {}: {}", record.topic(), ex.getMessage(), ex);
        }
    }

    // Getter cho SpEL KafkaListener
    public java.util.List<String> getTopics() {
        return kafkaProperties.getTopics();
    }
}
