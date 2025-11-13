package sum25.group03.instrumentservice.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreationTestOrderResponse {
    private Long id;
    private String code;
    private String barcode;
    private String status;
    private LocalDateTime createdAt;
    private boolean success;
    private String message;
}
