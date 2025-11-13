package sum25.group03.instrumentservice.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstrumentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeInstrumentModeRequest {
    @NotNull(message = "Instrument ID is required")
    private Long instrumentId;
    @NotNull(message = "New status is required")
    private InstrumentStatus newStatus;

    private String reason;

    private String qcCheckDetails;
}
