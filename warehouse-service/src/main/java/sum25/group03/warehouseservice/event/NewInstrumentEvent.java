package sum25.group03.warehouseservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewInstrumentEvent {
    private Long instrumentId;
    private String instrumentName;
    private ConfigEvent configEvent;
    private List<NewReagentEvent> newReagentEvents;
}
