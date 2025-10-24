package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateSpecificConfigReq {
    @NonNull
    private Long specificConfigurationId;
    private String supportedTests;
    private String parameterSettings;
    private String dataOutputFormat;
    private String communicationProtocol;
    private int mixingSpeed;
    private String firmwareVersion;
}
