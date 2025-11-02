package sum25.group03.warehouseservice.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentUsageDetailResponse {
    private Long usageId;
    private String reagentName;
    private Long quantityUsed;
    private String unit;
    private String usageType;
    private String instrumentName;
    private LocalDate usedAt;
    private String notes;
}
