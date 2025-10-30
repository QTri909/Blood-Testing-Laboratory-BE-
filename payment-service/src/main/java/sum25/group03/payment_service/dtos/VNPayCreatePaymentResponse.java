package sum25.group03.payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VNPayCreatePaymentResponse {
    private String paymentUrl;
    private String txnRef;
    private Long paymentRequestId;
    private Long amount;
    private String qrCodeBase64;
    private String qrCodeDataUrl;
    private boolean cachedInRedis;
    private Long cacheTTL;
}
