package sum25.group03.instrumentservice.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReagentValidationResponse {
    private Long reagentId;
    private String reagentName;
    private String unit;
    private String lotNumber;
    private String catalogNumber;
    private LocalDate expirationDate;
    private boolean isValid;
    private boolean isInInventory;
    private boolean isNotExpired;
    private String message;
}