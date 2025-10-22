package sum25.group03.warehouseservice.dto.internal;

import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ConfigType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConfigurationDTO {
    private String configurationKey;
    private String configurationValue;
    private String configurationCategory;
    private String instrumentType;
    private String description;
    private String unit;
    private ConfigType configType;
    private boolean active;
}
