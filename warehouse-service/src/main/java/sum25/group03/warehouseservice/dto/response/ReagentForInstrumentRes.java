package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReagentForInstrumentRes {
    private Long reagentId;
    private String reagentName;
    private String catalogNumber;
    private String casNumber;
    private String expirationDate;
    private double quantityUsed;
    private String unit;
    private String lotNumber;
    private LocalDate usedAt;
}
