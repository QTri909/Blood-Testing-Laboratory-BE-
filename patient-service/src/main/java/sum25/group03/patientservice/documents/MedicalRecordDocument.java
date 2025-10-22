package sum25.group03.patientservice.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

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
    private Long recordCode;
    private Long patientId;
    private Long assignedUser;
    private Long createdBy;
    private Long updatedBy;
    private String visitDate;
    private String createdAt;
    private String updatedAt;

    @Field(name = "recentAuditEntries")
    @DBRef(lazy = true) // only store references to AuditEntryDocument
    List<AuditEntryDocument> recentAuditEntries;
}
