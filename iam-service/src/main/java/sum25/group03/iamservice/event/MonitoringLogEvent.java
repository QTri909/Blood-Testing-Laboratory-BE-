package sum25.group03.iamservice.event;

import lombok.*;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonitoringLogEvent {
    private String action;
    private String operator;
    private String message;
    private String sourceService;
    private Map<String, Object> data;
}
