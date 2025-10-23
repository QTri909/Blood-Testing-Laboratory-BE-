package sum25.group03.patientservice.documents;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "medical_records")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDocument implements Serializable {

    @Id
    private String id;

    private Long recordId;
    private UUID recordCode;
    private Long patientId;
    private Long assignedUser;
    private Long createdBy;
    private Long updatedBy;
    private String visitDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Field(name = "recentAuditEntries")
    @DBRef(lazy = true) // only store references to AuditEntryDocument
    List<AuditEntryDocument> recentAuditEntries;

    @Override
    public String toString() {
        return "MedicalRecordDocument{" +
                "id='" + id + '\'' +
                ", recordId=" + recordId +
                ", recordCode=" + recordCode +
                ", patientId=" + patientId +
                ", assignedUser=" + assignedUser +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", visitDate='" + visitDate + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", recentAuditEntries=" + recentAuditEntries +
                '}';
    }
}
