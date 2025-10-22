package sum25.group03.instrumentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.event.ReagentInstalledEvent;
import sum25.group03.instrumentservice.service.KafkaEventPublisher;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisherImpl implements KafkaEventPublisher {

    private final KafkaTemplate<String, ReagentInstalledEvent> kafkaTemplate;
    private static final String TOPIC_NAME = "reagent-installed-events";

    @Override
    public void publishReagentInstalledEvent(ReagentInstalledEvent event) {
        try {
            log.info("Publishing reagent installed event for reagent ID: {} on instrument ID: {}",
                    event.getReagentId(), event.getInstrumentId());

            Message<ReagentInstalledEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                    .setHeader("reagentId", event.getReagentId().toString())
                    .build();

            kafkaTemplate.send(message);

            log.info("Reagent installed event published successfully for reagent ID: {}", event.getReagentId());
        } catch (Exception e) {
            log.error("Failed to publish reagent installed event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish reagent installed event", e);
        }
    }
}
