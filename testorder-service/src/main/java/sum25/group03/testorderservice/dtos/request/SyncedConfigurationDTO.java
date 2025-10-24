package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncedConfigurationDTO {
    private Long id;

    @NotBlank(message = "Config key cannot be blank")
    private String configKey;

    @NotNull(message = "Min value cannot be null")
    @DecimalMin(value = "0.0", message = "Min value must be greater than or equal to 0")
    private Double minValue;

    @NotNull(message = "Max value cannot be null")
    @DecimalMin(value = "0.0", message = "Max value must be greater than or equal to 0")
    private Double maxValue;
    private String description;
    private String unit;
}
