package sum25.group03.warehouseservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewInstrumentEvent {
    private Long instrumentId;
    private String instrumentName;
    private NewConfigEvent newConfigEvent;
    private List<NewReagentEvent> newReagentEvents;
}
