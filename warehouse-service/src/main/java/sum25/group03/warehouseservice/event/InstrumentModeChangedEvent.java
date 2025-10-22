package sum25.group03.warehouseservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentModeChangedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long instrumentId;
    private String instrumentCode;
    private String instrumentName;
    private String previousStatus;
    private String newStatus;
    private String reason;
    private LocalDate changedDate;
    private String eventTimestamp;
}
