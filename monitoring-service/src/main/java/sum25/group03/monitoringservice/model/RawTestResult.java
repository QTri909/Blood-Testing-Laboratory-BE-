package sum25.group03.monitoringservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "raw_test_backups")
public class RawTestResult {
    @Id
    private String id;
    private String testOrderId;
    private String instrumentId;
    private String hl7Payload;
    private Instant receivedAt;

}
