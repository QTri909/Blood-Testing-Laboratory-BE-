package sum25.group03.testorderservice.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestOrderPdfEmailRequest implements Serializable {
    private Long testOrderId;
    private String email; // receiver email address
    private String receiverName;
}
