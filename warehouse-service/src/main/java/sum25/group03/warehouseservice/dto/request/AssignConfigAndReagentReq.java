package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AssignConfigAndReagentReq {
    @NotNull
    private Long instrumentId;
    private Long configurationId;
    private List<Long> reagentIds;
}
