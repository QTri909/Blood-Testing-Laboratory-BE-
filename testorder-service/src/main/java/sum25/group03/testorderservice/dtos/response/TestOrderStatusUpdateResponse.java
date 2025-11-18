package sum25.group03.testorderservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.TestOrderStatus;

@Data
@AllArgsConstructor
@Builder
public class TestOrderStatusUpdateResponse {
    private Long testOrderId;
    private TestOrderStatus oldStatus;
    private TestOrderStatus newStatus;
}
