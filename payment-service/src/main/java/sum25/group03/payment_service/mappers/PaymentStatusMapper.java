package sum25.group03.payment_service.mappers;

import sum25.group03.payment_service.enums.PaymentRequestStatus;
import sum25.group03.payment_service.enums.PaymentTransactionStatus;

import java.util.EnumMap;
import java.util.Map;

public final class PaymentStatusMapper {

    private static final Map<PaymentTransactionStatus, PaymentRequestStatus> TRANSACTION_TO_REQUEST_MAP =
            new EnumMap<>(PaymentTransactionStatus.class);

    static {
        TRANSACTION_TO_REQUEST_MAP.put(PaymentTransactionStatus.PENDING, PaymentRequestStatus.PENDING);
        TRANSACTION_TO_REQUEST_MAP.put(PaymentTransactionStatus.PROCESSING, PaymentRequestStatus.PENDING);
        TRANSACTION_TO_REQUEST_MAP.put(PaymentTransactionStatus.SUCCESS, PaymentRequestStatus.SUCCESS);
        TRANSACTION_TO_REQUEST_MAP.put(PaymentTransactionStatus.FAILED, PaymentRequestStatus.FAILED);
        TRANSACTION_TO_REQUEST_MAP.put(PaymentTransactionStatus.REFUNDED, PaymentRequestStatus.FAILED);
        TRANSACTION_TO_REQUEST_MAP.put(PaymentTransactionStatus.EXPIRED, PaymentRequestStatus.EXPIRED);
        TRANSACTION_TO_REQUEST_MAP.put(PaymentTransactionStatus.CANCELED, PaymentRequestStatus.CANCELLED);
        TRANSACTION_TO_REQUEST_MAP.put(PaymentTransactionStatus.ERROR, PaymentRequestStatus.FAILED);
    }

    private PaymentStatusMapper() {
        // Utility class
    }

    public static PaymentRequestStatus toRequestStatus(PaymentTransactionStatus txnStatus) {
        return TRANSACTION_TO_REQUEST_MAP.getOrDefault(txnStatus, PaymentRequestStatus.FAILED);
    }
}