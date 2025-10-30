package sum25.group03.warehouseservice.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReagentRes {
    private Long reagentId;
    private String reagentName;
    private String catalogNumber;
    private String casNumber;
    private String unit;
    private String expirationDate;
    private int quantity;

    private List<ReagentUsageMiniRes> usages;
}
