package sum25.group03.warehouseservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentEvent {
    private Long instrumentId;
    private String instrumentName;
    private String eventType;
    private String performedBy;
    private InstrumentStatus status;
    private LocalDate timestamp;
    private String details;
}
