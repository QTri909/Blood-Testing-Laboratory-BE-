package sum25.group03.patientservice.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Builder
@Getter
public class BusinessLogicException extends RuntimeException {
    private String customCode;
    private String reason;

    public BusinessLogicException(String message, String reason) {
        super(message);
        this.reason = Objects.requireNonNullElse(reason, "BUSINESS_LOGIC_VIOLATION");
    }
}
