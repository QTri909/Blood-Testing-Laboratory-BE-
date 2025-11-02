package sum25.group03.payment_service.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.payment_service.enums.PaymentTransactionStatus;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionRequest {

//    @NotBlank(message = "Gateway transaction ID is required")
    private String gatewayTransactionId;

    private Map<String, Object> rawResponse;

//    @NotNull(message = "Status is required")
    private PaymentTransactionStatus status;

//    @NotBlank(message = "Payment request ID is required")
    private String paymentRequestId;
}