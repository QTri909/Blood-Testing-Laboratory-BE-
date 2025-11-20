package sum25.group03.payment_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransactionRes implements Serializable {
    private String id;
    private String paymentRequestId;
    private String gatewayTransactionId;
    private String status;
    private String gatewayStatusCode;
    // private Object  rawResponse;
    private String createdAt;
    private String updatedAt;
}
