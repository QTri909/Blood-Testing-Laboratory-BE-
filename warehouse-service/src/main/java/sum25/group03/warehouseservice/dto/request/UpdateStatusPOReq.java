package sum25.group03.warehouseservice.dto.request;

import lombok.*;
import sum25.group03.warehouseservice.entity.enums.SupplyStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateStatusPOReq {
    private String purchaseOrderNumber;
    private SupplyStatus supplyStatus;
}
