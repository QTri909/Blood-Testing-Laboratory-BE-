package sum25.group03.warehouseservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GlobalConfigRes {
    private BigDecimal sampleVolume;
    private int maxConcurrentSamples;
    private String sampleVolumeUnit;
    private int defaultTimeout;
}
