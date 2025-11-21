package sum25.group03.payment_service.dtos.request;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class PaymentEmailHelperDTO  {
    private String receiverName;
    private final String receiverEmail;
    private final String paymentUrl;
    private String additionalInfo;
    private final String orderCode;
}
