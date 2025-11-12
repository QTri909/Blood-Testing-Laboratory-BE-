package sum25.group03.monitoringservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "health_check_logs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthCheckLog {
    @Id
    private String id;
    private Instant timestamp;
    private String status;
}
