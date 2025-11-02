package sum25.group03.patientservice.entities.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "clinical_notes")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ESClinicalNote {

    @Id
    private Long noteId;
    private Long recordId;
    private Long notedBy;
    private String note;
    private String createdAt;
    private String updatedAt;

    // ManyToOne with ESMedicalRecord
    private ESMedicalRecord medicalRecord; // embedded document
}
