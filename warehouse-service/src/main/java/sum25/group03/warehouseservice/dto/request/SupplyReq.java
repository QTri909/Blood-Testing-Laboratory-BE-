package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ReagentUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SupplyReq {
    @NotNull
    @Positive(message = "quantityReceived must be positive")
    private double quantityReceived;
    @NotBlank
    private String lotNumber;
    @NotBlank
    private ReagentUnit unitOfMeasurement;
    @NotNull
    private LocalDate expiryDate;
    @NotNull
    private LocalDate manufactureDate;
    @Size(max = 500)
    private String notes;
    @NotNull
    private Long reagentId;
}
