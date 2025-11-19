package sum25.group03.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;

import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.event.TestResultPublishedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private static final String TOPIC = "complete-sync-test-result-events";

    private final KafkaTemplate<String, TestResultPublishedEvent> kafkaTemplate;

    public void publishCompleteSyncTestResultEvent(TestResultPublishedEvent event) {
        try {
            log.info("Publishing complete sync test result event");

            Message<TestResultPublishedEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    .setHeader("eventType", "CompleteSyncTestResult")
                    .build();

            kafkaTemplate.send(message);

            log.info("Complete sync test result event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish complete sync test result event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish complete sync test result event", e);
        }
    }


}
