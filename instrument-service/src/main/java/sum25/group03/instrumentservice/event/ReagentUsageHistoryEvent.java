package sum25.group03.instrumentservice.event;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.model.Instrument;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentUsageHistoryEvent implements Serializable {
    private Long instrumentId;
    private Long testOrderId;
    private Double quantityUsed;
    private String unit;
    private String usageType;
    private Integer lotReagentId;
    private Long reagentId;
    private String reagentName;
    private String lotNumber;
    private Integer usedBy;
    private LocalDate usedAt;
    private String notes;
}
