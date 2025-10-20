package sum25.group03.warehouseservice.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConfigurationsReq {
    private String configurationKey;
    private String configurationValue;
    private String configurationCategory;
    private String instrumentType;
    private String description;
    private String unit;
}
