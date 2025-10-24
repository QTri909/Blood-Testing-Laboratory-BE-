package sum25.group03.patientservice.documents;

import jakarta.persistence.PrePersist;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import sum25.group03.patientservice.enums.MedicalRecordStatus;

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
    private MedicalRecordStatus status; // ACTIVE, INACTIVE, DELETED, HIDDEN,..

    @Field(name = "recentAuditEntries")
    @DBRef(lazy = true) // only store references to AuditEntryDocument
    List<AuditEntryDocument> recentAuditEntries;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = MedicalRecordStatus.ACTIVE;
        }
    }

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
                ", status=" + status +
                '}';
    }
}
