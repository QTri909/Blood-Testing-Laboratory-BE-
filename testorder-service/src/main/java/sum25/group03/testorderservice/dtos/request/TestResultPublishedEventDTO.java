package sum25.group03.testorderservice.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultPublishedEventDTO {
    private Long testOrderId;
    private Long instrumentId;
    private String barcode;
    private String rawData;
    private String hl7Message;
    private LocalDateTime timestamp;
    private String status;
}
