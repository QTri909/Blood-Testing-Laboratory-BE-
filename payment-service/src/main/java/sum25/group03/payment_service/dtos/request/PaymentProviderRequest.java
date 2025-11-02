package sum25.group03.payment_service.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.payment_service.enums.PaymentProviderCode;
import sum25.group03.payment_service.enums.PaymentProviderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProviderRequest {

//    @NotNull(message = "Payment provider code is required")
    private PaymentProviderCode code;

//    @NotBlank(message = "Name is required")
    private String name;

//    @NotNull(message = "Status is required")
    private PaymentProviderStatus status;
}