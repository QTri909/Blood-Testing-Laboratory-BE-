package sum25.group03.warehouseservice.dto.internal;

import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ConfigType;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConfigurationDTO {
    private BigDecimal sampleVolume;
    private int maxConcurrentSamples;
    private String supportedTests;
    private ConfigType configType;
    private String parameterSettings;
    private String description;
    private String sampleVolumeUnit;
}
