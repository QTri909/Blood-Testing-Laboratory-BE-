package sum25.group03.instrumentservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstrumentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeInstrumentModeResponse {
    private Long instrumentId;
    private String instrumentName;
    private InstrumentStatus previousStatus;
    private InstrumentStatus newStatus;
    private String reason;
    private LocalDateTime changedAt;
    private String message;
}
