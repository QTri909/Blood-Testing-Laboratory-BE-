package sum25.group03.payment_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.payment_service.enums.PaymentProviderCode;
import sum25.group03.payment_service.enums.ProviderStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProviderResponse {

    private String id;
    private PaymentProviderCode code;
    private String name;
    private ProviderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}