package sum25.group03.patientservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditEntryResponseDTO implements Serializable {
    private String id;
    private String entityType;
    private Long entityId;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private String changedAt;
    private Long changedBy;
}
