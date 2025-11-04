package sum25.group03.testorderservice.dtos.response;

import lombok.Builder;
import lombok.Data;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreationTestOrderResponse {
    private Long id;
    private UUID code;
    private String barcode;
    private TestOrderStatus status;
    private LocalDateTime createdAt;
}
