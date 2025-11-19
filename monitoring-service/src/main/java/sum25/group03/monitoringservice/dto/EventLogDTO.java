package sum25.group03.monitoringservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventLogDTO {
    private String id;
    private String action;
    private String message;
    private Instant timestamp;
    private String sourceService;
}
