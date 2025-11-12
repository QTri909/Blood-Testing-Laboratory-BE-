package sum25.group03.patientservice.dtos.response;

import lombok.*;
import sum25.group03.patientservice.enums.MedicalRecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalRecordResponse {
    private Long recordId;
    private Long patientId;
    private Long assignedUser;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime visitDate;
    private UUID recordCode;
    private String patientName;
    private String assignedUserName;
    private MedicalRecordStatus status;
}
