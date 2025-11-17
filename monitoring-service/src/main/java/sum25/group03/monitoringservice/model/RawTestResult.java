package sum25.group03.monitoringservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

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
    private String hl7PMessage;
    private Instant receivedAt;
    private String barcode;
    private String rawData;
    private String status;
}
