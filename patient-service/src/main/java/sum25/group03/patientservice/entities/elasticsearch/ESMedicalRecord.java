package sum25.group03.patientservice.entities.elasticsearch;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import sum25.group03.patientservice.enums.MedicalRecordStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(indexName = "medical_records")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ESMedicalRecord {
    @Id
    private Long recordId;
    private UUID recordCode;
    private Long patientId;
    private Long assignedUser;
    private MedicalRecordStatus status;
    private LocalDateTime visitDate;
    private Long createdBy;
    private Long updatedBy;

    @Field(type = FieldType.Nested)
    private List<ESClinicalNote> clinicalNotes;
}
