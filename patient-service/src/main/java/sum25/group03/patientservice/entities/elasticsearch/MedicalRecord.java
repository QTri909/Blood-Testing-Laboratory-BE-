package sum25.group03.patientservice.entities.elasticsearch;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import sum25.group03.patientservice.enums.MedicalRecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(indexName = "medical_records")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalRecord {
    @Id
    private Long recordId;
    private UUID recordCode;
    private Long patientId;
    private Long assignedUser;
    private MedicalRecordStatus status;
    private LocalDateTime visitDate;
    private Long createdBy;
    private Long updatedBy;
}
