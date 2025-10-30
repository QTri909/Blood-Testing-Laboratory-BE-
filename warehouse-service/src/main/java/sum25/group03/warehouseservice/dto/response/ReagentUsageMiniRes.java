package sum25.group03.warehouseservice.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReagentUsageMiniRes {
    private Long usageId;
    private String usageType;
    private Long quantityUsed;
    private String unit;
    private String usedAt;
    private String performedBy;
}
