package sum25.group03.payment_service.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.payment_service.enums.StandardCurrency;
import sum25.group03.payment_service.enums.PaymentRequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestResponse {

    private String id;
    private Double amount;
    private StandardCurrency standardCurrency;
    private String orderCode;
    private PaymentRequestStatus status;
    private Long userId;
    private PaymentProviderResponse paymentProvider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
