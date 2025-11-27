package sum25.group03.common.response.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignConfigAndReagentEvent {
    private Long instrumentId;
    private ConfigEvent configEvent;
    private List<NewReagentEvent> reagentEvents;
}
