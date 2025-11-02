package sum25.group03.warehouseservice.exception;

public class MissingRequiredFieldsException extends RuntimeException{
    public MissingRequiredFieldsException(String message) {
        super(message);
    }
}
