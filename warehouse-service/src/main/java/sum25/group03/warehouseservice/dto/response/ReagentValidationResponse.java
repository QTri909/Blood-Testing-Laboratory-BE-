package sum25.group03.warehouseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReagentValidationResponse {
    private Long reagentId;
    private String reagentName;
    private String batchNumber;
    private String catalogNumber;
    private LocalDate expirationDate;
    private ReagentStatus status;
    private boolean isValid;
    private boolean isInInventory;
    private boolean isNotExpired;
    private String message;
    private String vendorName;
}
