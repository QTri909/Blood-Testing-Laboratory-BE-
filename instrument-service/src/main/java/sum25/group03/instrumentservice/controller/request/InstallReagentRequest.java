package sum25.group03.instrumentservice.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallReagentRequest {
    @NotNull(message = "Instrument ID is required")
    private Long instrumentId;
    @NotNull(message = "Reagent ID is required")
    private Long reagentId;
    @NotBlank(message = "Batch number/barcode is required")
    private String lotNumber;

    @NotNull(message = "Current volume is required")
    private Double currentVolume;
}
