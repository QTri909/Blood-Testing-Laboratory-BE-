package sum25.group03.monitoringservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.monitoringservice.model.RawTestResult;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncUpEvent {
    private String orderId;
    private List<RawTestResult> rawResults;
}
