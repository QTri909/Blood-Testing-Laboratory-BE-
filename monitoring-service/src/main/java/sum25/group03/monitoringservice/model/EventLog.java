package sum25.group03.monitoringservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection  = "event_logs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventLog {
    @Id
    private String id;

    private String topic;
    private String action;
    private String message;
    private String operator;
    private Instant createdAt;

    private Map<String, Object> payload;
}
