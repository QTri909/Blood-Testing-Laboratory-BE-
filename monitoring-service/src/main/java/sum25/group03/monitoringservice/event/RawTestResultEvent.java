package sum25.group03.monitoringservice.event;

import lombok.*;
import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawTestResultEvent {
    private String orderId;
    private String instrumentId;
    private Map<String, Object> resultData;
    private Instant timestamp;
}
