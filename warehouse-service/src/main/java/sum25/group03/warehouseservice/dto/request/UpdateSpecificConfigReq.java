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
    private Long instrumentId;
    @NonNull
    private BigDecimal sampleVolume;
    @Positive
    private int maxConcurrentSamples;
    @NotBlank
    private String supportedTests;
    @NotBlank
    private String parameterSettings;
    private String description;
    @NotBlank
    private String sampleVolumeUnit;
}
