package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateConfigReq {
    @NonNull
    private Long configurationId;
    private String supportedTests;
    private String dataOutputFormat;
    private String communicationProtocol;
    private int mixingSpeed;
    private String firmwareVersion;
    private int usePerRun;
}
