package sum25.group03.warehouseservice.dto.internal;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConfigIdAndReagentDTO {
    private Long configurationId;
    private Long reagentId;
}
