package sum25.group03.testorderservice.dtos.response;

import lombok.Builder;
import lombok.Data;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class CreationTestOrderResponse {
    private Long id;
    private String code;
    private String barcode;
    private TestOrderStatus status;
    private LocalDateTime createdAt;
}
