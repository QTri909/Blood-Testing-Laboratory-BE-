package sum25.group03.payment_service.dtos.request;

import lombok.Data;

@Data
public class StripeConfirmRequest {
    private String paymentIntentId;
    private Long orderId; // nếu cần
    private Long amount;  // nếu cần
    private String currency; // nếu cần
}
