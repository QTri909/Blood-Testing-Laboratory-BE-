package sum25.group03.instrumentservice.exception;

public class WarehouseServiceException extends RuntimeException {
    public WarehouseServiceException(String message) {
        super(message);
    }

    public WarehouseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
