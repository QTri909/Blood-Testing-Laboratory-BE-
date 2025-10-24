package sum25.group03.instrumentservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstrumentStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrumentResponse {
    private Long id;
    private String instrumentName;
    private InstrumentStatus status;
    private Long configurationId;
    private String configurationName;
    private List<InstalledReagentResponse> installedReagents;
}
