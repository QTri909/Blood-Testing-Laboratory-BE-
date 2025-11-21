package sum25.group03.common.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.events.MonitoringLogEvent;

@Service
@RequiredArgsConstructor
public class MonitoringLogProducer {

    private final KafkaTemplate<String, MonitoringLogEvent> commonMonitoringLogKafkaTemplate;

    private static final String TOPIC = "monitoring-log";

    public void sendMonitoringLog(MonitoringLogEvent event) {
        Message<MonitoringLogEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("action", event.getAction())
                .setHeader("operator", event.getOperator())
                .setHeader("message", event.getMessage())
                .build();

        commonMonitoringLogKafkaTemplate.send(message);
    }
}
