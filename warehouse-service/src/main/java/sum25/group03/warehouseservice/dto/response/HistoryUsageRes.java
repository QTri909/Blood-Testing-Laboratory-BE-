package sum25.group03.warehouseservice.dto.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HistoryUsageRes {
    private Long usageId;
    private ReagentRes reagent;
    private String usageType;
    private Double quantityUsed;
    private String unit;
    private LocalDate usedAt;
    private int usedBy;
    private String lotNumber;
    private String notes;
    private String instrumentName;
}
