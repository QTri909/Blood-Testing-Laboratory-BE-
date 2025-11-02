package sum25.group03.payment_service.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.payment_service.enums.PaymentTransactionStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionResponse {

    private UUID id;
    private String gatewayTransactionId;
    private Map<String, Object> rawResponse;
    private PaymentTransactionStatus status;
    private PaymentRequestResponse paymentRequest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}