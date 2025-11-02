package sum25.group03.instrumentservice.client.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreationTestOrderResponse {
    private Long id;
    private String code;
    private String barcode;
    private String status;
    private LocalDateTime createdAt;
}
