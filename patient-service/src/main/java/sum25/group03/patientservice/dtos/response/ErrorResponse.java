package sum25.group03.patientservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Builder
@ToString
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    private String message;
    private String reason;
    private Map<String, String> details;
}
