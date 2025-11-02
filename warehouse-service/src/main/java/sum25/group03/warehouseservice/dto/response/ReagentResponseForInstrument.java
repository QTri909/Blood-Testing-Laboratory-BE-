package sum25.group03.warehouseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReagentResponseForInstrument {
    private Long reagentId;
    private String unit;
    private String reagentName;
    private Double usageMax;
    private Double usageMin;
}
