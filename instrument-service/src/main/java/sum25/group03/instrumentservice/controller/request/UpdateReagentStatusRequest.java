package sum25.group03.instrumentservice.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReagentStatusRequest {
    private Long installedReagentId;
    private Long instrumentId;
    private InstalledReagentStatus newStatus;
    private Integer changedBy;
    private String reason;
}

