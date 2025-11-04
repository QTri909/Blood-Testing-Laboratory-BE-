package sum25.group03.payment_service.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VNPayCreatePaymentRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String orderCode;
    @Min(1)
    private long amount; // in VND
    private String orderInfo;
    private String bankCode;
    private String locale = "vn";
    private boolean generateQRCode = false;
    private Integer qrWidth;
    private Integer qrHeight;
}
