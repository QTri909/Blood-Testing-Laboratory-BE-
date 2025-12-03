package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateConfigReq {
    @NotNull
    private Long configurationId;
    @NotBlank
    private String configurationName;
    @NotBlank
    private String supportedTests;
    @NotBlank
    private String dataOutputFormat;
    @NotBlank
    private String communicationProtocol;
    @Positive
    private int mixingSpeed;
    @NotBlank
    private String firmwareVersion;
    @Positive
    private Double loadThreshold;
}
