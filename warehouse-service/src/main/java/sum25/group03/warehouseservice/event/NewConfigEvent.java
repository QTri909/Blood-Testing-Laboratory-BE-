package sum25.group03.warehouseservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewConfigEvent {
    private BigDecimal sampleVolume;
    private String sampleVolumeUnit;
    private int maxConcurrentSamples;
    private int defaultTimeout;
    private String supportedTests;
    private String parameterSettings;
    private String dataOutputFormat;
    private String communicationProtocol;
    private int  mixingSpeed;
    private String firmwareVersion;
}
