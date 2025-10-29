package sum25.group03.warehouseservice.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReagentForInstrumentRes {
    private Long reagentId;
    private String reagentName;
    private String catalogNumber;
    private String casNumber;
    private String unit;
    private String expirationDate;
}
