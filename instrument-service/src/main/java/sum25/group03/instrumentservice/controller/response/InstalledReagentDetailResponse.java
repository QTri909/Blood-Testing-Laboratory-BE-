package sum25.group03.instrumentservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstalledReagentDetailResponse {
    private Long id;
    private Long instrumentId;
    private String instrumentName;
    private Integer lotReagentId;
    private Double currentVolume;
    private InstalledReagentStatus status;
    private LocalDate installationDate;
}
