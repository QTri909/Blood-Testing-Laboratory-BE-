package sum25.group03.payment_service.exceptions;

public class VNPayInvalidSignatureException extends RuntimeException {
    public VNPayInvalidSignatureException(String message) {
        super(message);
    }
}
