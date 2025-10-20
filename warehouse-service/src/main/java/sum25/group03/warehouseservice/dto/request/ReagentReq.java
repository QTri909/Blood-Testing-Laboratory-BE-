package sum25.group03.warehouseservice.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReagentReq {
    private String reagentName;
    private String expirationDate;
    private String storageConditions;
    private String quantity;
    private String unit;
    private String batchNumber;
    private String catalogNumber;
    private String casNumber;
}
