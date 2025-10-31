package sum25.group03.payment_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.payment_service.enums.TransactionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionRequest {

//    @NotBlank(message = "Gateway transaction ID is required")
    private String gatewayTransactionId;

    private String rawResponse;

//    @NotNull(message = "Status is required")
    private TransactionStatus status;

//    @NotBlank(message = "Payment request ID is required")
    private String paymentRequestId;
}