package sum25.group03.common.response.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultPublishedEvent implements Serializable {
    private Long testOrderId;
    private Long instrumentId;
    private String barcode;
    private String rawData;
    private String hl7Message;
    private LocalDateTime timestamp;
    private String status;
}
