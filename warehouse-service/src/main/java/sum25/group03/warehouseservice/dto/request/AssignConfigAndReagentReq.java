package sum25.group03.warehouseservice.dto.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AssignConfigAndReagentReq {
    private Long instrumentId;
    private Long specificConfigurationId;
    private List<Long> reagentIds;
}
