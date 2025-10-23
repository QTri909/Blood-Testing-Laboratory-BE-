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
public class InstallReagentResponse {
    private Long installedReagentId;
    private Long instrumentId;
    private Long reagentId;
    private String instrumentName;
    private String reagentName;
    private String lotNumber;
    private String unit;
    private Double currentVolume;
    private  LocalDate expirationDate;
    private LocalDate installationDate;
    private InstalledReagentStatus status;
    private String message;
    private boolean success;
}
