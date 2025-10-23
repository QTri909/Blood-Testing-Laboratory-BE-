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
public class CreateInstrumentRequest {

    @NotBlank(message = "Instrument name is required")
    private String instrumentName;

    @NotNull(message = "Status is required")
    private InstrumentStatus status;

    @NotNull(message = "Configuration ID is required")
    private Integer configurationId;
}
