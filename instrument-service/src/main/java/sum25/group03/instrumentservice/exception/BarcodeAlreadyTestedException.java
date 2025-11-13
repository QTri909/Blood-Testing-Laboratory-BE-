package sum25.group03.instrumentservice.exception;

public class BarcodeAlreadyTestedException extends RuntimeException {
    public BarcodeAlreadyTestedException(String message) {
        super(message);
    }
}
