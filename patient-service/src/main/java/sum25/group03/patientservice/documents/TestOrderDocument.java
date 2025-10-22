package sum25.group03.patientservice.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "test_orders")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestOrderDocument {

    @Id
    private String id;

    private Long testOrderId;
    private Long medicalRecordId;
    private Long patientId;
    private Long createdBy;
    private Long runBy;
    private String createdAt;
    private String updatedAt;
    private String runDate;
    private String status;

    @Field(name = "recentAuditEntries")
    @DBRef(lazy = true) // only store references to AuditEntryDocument
    List<AuditEntryDocument> recentAuditEntries;
}
