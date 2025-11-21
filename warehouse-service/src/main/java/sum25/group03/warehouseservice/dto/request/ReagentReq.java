package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReagentReq {
    @NotBlank
    private String reagentName;

    @NotBlank
    private String catalogNumber;

    @NotBlank
    private String casNumber;

    @NotBlank
    private String unit;

    @NotBlank
    private String storageConditions;

    @NotNull
    private Integer maxStockLevel;

    @NotNull
    private Integer minStockLevel;

    private Double usageMin;
    private Double usageMax;
}
