package sum25.group03.instrumentservice.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawTestResultResponse {
    private Long resultId;
    private Long testOrderId;
    private Long instrumentId;
    private Map<String, Double> rawData;
    private String hl7Message;
    private Boolean isSentToMonitoring;
    private Boolean isSynced;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
