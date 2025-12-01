package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;
import sum25.group03.warehouseservice.entity.enums.ReagentUnit;

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

    @NotNull
    private ReagentUnit unit;

    @NotBlank
    private String storageConditions;

    private Integer maxStockLevel;

    private Integer minStockLevel;

    @NotNull
    private Double usageMin;

    @NotNull
    private Double usageMax;
}
