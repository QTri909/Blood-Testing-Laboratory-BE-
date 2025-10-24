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
    private Long globalConfigurationId;
    private String supportedTests;
    private String parameterSettings;
    private String dataOutputFormat;
    private String communicationProtocol;
    private int  mixingSpeed;
}
