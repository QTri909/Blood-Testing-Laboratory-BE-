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
public class SpecificConfigReq {
    @NonNull
    private Long globalConfigurationId;
    @NotBlank
    private String supportedTests;
    @NotBlank
    private String parameterSettings;
    @NotBlank
    private String dataOutputFormat;
    @NotBlank
    private String communicationProtocol;
    @Positive
    private int mixingSpeed;
    @NotBlank
    private String firmwareVersion;

}
