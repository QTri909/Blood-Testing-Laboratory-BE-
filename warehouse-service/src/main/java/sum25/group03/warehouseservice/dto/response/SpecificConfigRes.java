package sum25.group03.warehouseservice.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SpecificConfigRes {

    private String supportedTests;
    private String parameterSettings;
    private String dataOutputFormat;
    private String communicationProtocol;
    private int mixingSpeed;
    private String firmwareVersion;
}
