package sum25.group03.instrumentservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstalledReagentResponse {
    private Long id;
    private Double currentVolume;
    private InstalledReagentStatus status;
    private LocalDate installationDate;
    private Integer lotReagentId;
}