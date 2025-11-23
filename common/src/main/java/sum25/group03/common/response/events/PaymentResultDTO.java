package sum25.group03.common.response.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentResultDTO implements Serializable {
    private String orderCode;
    private String status; // PENDING, SUCCESS, FAILED, CANCELLED, EXPIRED
    private String transactionStatus; // PENDING, SUCCESS, FAILED, REFUNDED, EXPIRED, CANCELED, COMPLETED, ERROR, PROCESSING
}
