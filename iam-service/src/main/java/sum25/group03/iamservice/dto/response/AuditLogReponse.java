package sum25.group03.iamservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogReponse {
    private String action;
    private String entityName;
    private Long entityId;
    private String performedBy;
    private LocalDateTime timestamp;
    private String details;
}
