package sum25.group03.payment_service.enums;

public enum PaymentTransactionStatus {
    PENDING,       // waiting for payment confirmation
    SUCCESS,       // transaction succeeded
    FAILED,        // general failure
    REFUNDED,      // money returned
    EXPIRED,       // timeout or expired payment
    CANCELED,      // customer canceled manually
    ERROR,         // system or unknown error
    PROCESSING     // intermediate state (bank confirmation pending)
}