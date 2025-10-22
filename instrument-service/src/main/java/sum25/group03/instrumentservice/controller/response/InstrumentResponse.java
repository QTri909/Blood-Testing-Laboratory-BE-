package sum25.group03.instrumentservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstrumentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrumentResponse {
    private Integer id;
    private String instrumentCode;
    private String instrumentName;
    private InstrumentStatus status;
    private Integer configurationId;
    private String configurationName;
}
