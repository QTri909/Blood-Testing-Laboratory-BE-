package sum25.group03.payment_service.helpers;

import sum25.group03.payment_service.enums.PaymentTransactionStatus;

import java.util.Map;

public class VNPayHelpers {

    public static PaymentTransactionStatus mapVnPayResponseFromParams(Map<String, String> params) {
        String rsp = params.getOrDefault("vnp_ResponseCode", "99");
        return mapVnPayResponseFromResponseCode(rsp);
    }

    public static PaymentTransactionStatus mapVnPayResponseFromResponseCode(String responseCode) {
        return switch (responseCode) {
            case "00" -> PaymentTransactionStatus.SUCCESS;           // success
            case "07" -> PaymentTransactionStatus.PROCESSING;        // flagged / suspected fraud
            case "09", "10", "11", "12", "13", "51", "65" ->
                    PaymentTransactionStatus.FAILED;                 // bank or auth issue
            case "24" -> PaymentTransactionStatus.CANCELED;          // user canceled
            case "75" -> PaymentTransactionStatus.ERROR;             // bank maintenance
            case "99" -> PaymentTransactionStatus.ERROR;             // general error
            default -> PaymentTransactionStatus.FAILED;              // fallback for all else
        };
    }

}
