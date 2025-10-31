package sum25.group03.instrumentservice.exception;

public class InstrumentNotReadyException extends RuntimeException {
    public InstrumentNotReadyException(String message) {
        super(message);
    }
}
