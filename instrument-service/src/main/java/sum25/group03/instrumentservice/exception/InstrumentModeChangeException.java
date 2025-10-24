package sum25.group03.instrumentservice.exception;

public class InstrumentModeChangeException extends RuntimeException {
    public InstrumentModeChangeException(String message) {
        super(message);
    }

    public InstrumentModeChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}