package sum25.group03.patientservice.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "test_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDocument {

    @Id
    private String id;

    private Long testResultId;
    private Long testOrderId;
    private Long patientId;
    private Long medicalRecordId;
    private Long instrumentId;
    private Long parameterId;
    private String flagStatus;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String resultValue;
    private String testType;

    @Field(name = "recentAuditEntries")
    @DBRef(lazy = true) // only store references to AuditEntryDocument
    List<AuditEntryDocument> recentAuditEntries;
}
