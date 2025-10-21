package sum25.group03.monitoringservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.service.MonitoringService;

@Slf4j
@Service
public class KafkaConsumer {
    private final MonitoringService monitoringService;
    private final String topic="test";
    public KafkaConsumer(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }
    @KafkaListener(topics = topic, groupId = "monitoring-service")
    public void consumeEvent(String message) {
        try {
            log.info("consume message: {}", message);
            ObjectMapper mapper = new ObjectMapper();
            EventLog event = mapper.readValue(message, EventLog.class);
            monitoringService.addEventLog(event);
        } catch (Exception e) {
            log.error("consume event: " + message, e);
        }
    }
}
