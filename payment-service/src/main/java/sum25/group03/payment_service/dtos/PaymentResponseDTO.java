package sum25.group03.payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private Long paymentRequestId;
    private String orderCode;
    private String status;
    private String message;
    private Long amount;
    private String transactionId;
    private String dataSource; // REDIS | DATABASE | VNPAY_API | VALIDATION
    private String txnRef;
}
