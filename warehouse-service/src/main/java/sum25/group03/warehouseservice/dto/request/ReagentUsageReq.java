package sum25.group03.warehouseservice.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReagentUsageReq {
    private Long reagentId;
    private double quantity;
    private Long userId;
    private String lotNumber;
    private Long instrumentId;
    private String notes;
}
