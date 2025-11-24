package sum25.group03.common.response.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentEvent {
    private Long instrumentId;
    private String instrumentName;
    private String eventType;
    private String performedBy;
    private String status;
    private LocalDate timestamp;
    private String details;
}
