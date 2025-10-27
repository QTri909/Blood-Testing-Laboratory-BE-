package sum25.group03.instrumentservice.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstrumentStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodTestingRequest {
    @NotNull(message = "Instrument ID is required")
    private Long instrumentId;
    @NotNull(message = "Test Order ID is required")
    private Long testOrderId;

    @NotBlank(message = "Barcode is required")
    private String barcode;
    @NotNull
    private List<String> testTypes;

}
