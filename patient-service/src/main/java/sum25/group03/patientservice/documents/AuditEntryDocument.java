package sum25.group03.patientservice.documents;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sum25.group03.patientservice.enums.DocumentType;

import java.time.LocalDateTime;

@Document(collection = "audit_entries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditEntryDocument {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private DocumentType entityType;

    private Long entityId;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;
    private Long changedBy;

    @Override
    public String toString() {
        return "AuditEntryDocument{" +
                "id='" + id + '\'' +
                ", entityType=" + entityType +
                ", entityId='" + entityId + '\'' +
                ", fieldChanged='" + fieldChanged + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", changedAt='" + changedAt + '\'' +
                ", changedBy='" + changedBy + '\'' +
                '}';
    }
}
