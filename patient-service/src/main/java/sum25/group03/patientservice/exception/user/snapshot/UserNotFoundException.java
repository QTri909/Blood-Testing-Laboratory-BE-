package sum25.group03.patientservice.exception.user.snapshot;

import lombok.Builder;
import sum25.group03.patientservice.exception.BusinessLogicException;

public class UserNotFoundException extends BusinessLogicException {
    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND");
    }
    public UserNotFoundException(String message, String reason) {
        super(message, reason);
    }
}
