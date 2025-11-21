package sum25.group03.common.response.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
