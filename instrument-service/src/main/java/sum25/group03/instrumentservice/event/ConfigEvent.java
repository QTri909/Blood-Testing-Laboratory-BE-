package sum25.group03.instrumentservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigEvent {
    private String supportedTests;
    private String dataOutputFormat;
    private String communicationProtocol;
    private int  mixingSpeed;
    private String firmwareVersion;
    private int usePerRun;
}
