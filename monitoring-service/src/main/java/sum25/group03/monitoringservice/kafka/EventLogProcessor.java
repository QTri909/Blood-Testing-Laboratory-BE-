package sum25.group03.monitoringservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.repository.EventLogRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.json.JSONObject;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class EventLogProcessor {
    @Autowired
    private EventLogRepository repository;

    public void process(ConsumerRecord<String, String> record) {
        // Parse raw message (JSON)
        String rawPayload = record.value();
        JSONObject json = new JSONObject(rawPayload);

        EventLog logEntry = new EventLog();
        logEntry.setAction(json.optString("action", "UNKNOWN"));
        logEntry.setOperator(json.optString("operator", "system")
        );
        logEntry.setMessage(json.optString("message", ""));
        logEntry.setSourceService(json.optString("sourceService", "UNKNOWN"));
        logEntry.setData(json.optJSONObject("data") != null ? json.optJSONObject("data").toMap() : Map.of());

        logEntry.setTimestamp(Instant.ofEpochMilli(record.timestamp()));
        logEntry.setChecksum(calculateChecksum(rawPayload));

        repository.save(logEntry);
        log.info("EventLog saved for topic {} action {}", record.topic(), logEntry.getAction());
    }
    private String extractService(String topic) {
        // e.g. topic = test.order.created -> "test-order-service"
        return topic.split("\\.")[0] + "-service";
    }

    private String calculateChecksum(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return "SHA256:" + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return "ERROR";
        }
    }

}
