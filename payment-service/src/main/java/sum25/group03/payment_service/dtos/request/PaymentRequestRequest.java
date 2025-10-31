package sum25.group03.payment_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.payment_service.enums.Currency;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestRequest {

//    @NotNull(message = "Amount is required")
//    @Positive(message = "Amount must be positive")
    private Double amount;

//    @NotNull(message = "Currency is required")
    private Currency currency;

//    @NotBlank(message = "Order code is required")
    private String orderCode;

//    @NotNull(message = "User ID is required")
    private Long userId;

//    @NotBlank(message = "Payment provider ID is required")
    private String paymentProviderId;
}