package sum25.group03.instrumentservice.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReagentResponse {
    private Long reagentId;
    private String unit;
    private String reagentName;
    private Double usageMax;
    private Double usageMin;
}
