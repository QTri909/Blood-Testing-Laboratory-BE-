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
public class UpdateGlobalConfigReq {
    @NonNull
    private Long globalConfigurationId;
    private BigDecimal sampleVolume;
    private int maxConcurrentSamples;
    private String sampleVolumeUnit;
    private int defaultTimeout;
}
