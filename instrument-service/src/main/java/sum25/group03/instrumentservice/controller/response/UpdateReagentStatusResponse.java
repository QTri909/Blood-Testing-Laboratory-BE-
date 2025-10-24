package sum25.group03.instrumentservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReagentStatusResponse {
    private Long installedReagentId;
    private Long instrumentId;
    private String instrumentName;
    private InstalledReagentStatus previousStatus;
    private InstalledReagentStatus newStatus;
    private LocalDateTime changedAt;
    private Integer changedBy;
    private String reason;
    private String message;
    private boolean success;
}