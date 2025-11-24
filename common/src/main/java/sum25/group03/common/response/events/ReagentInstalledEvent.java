package sum25.group03.common.response.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentInstalledEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long reagentId;
    private String reagentName;
    private String lotNumber;
    private Double requiredVolume;
    private Long instrumentId;
    private String instrumentName;
    private LocalDate installationDate;
    private String eventTimestamp;
}
