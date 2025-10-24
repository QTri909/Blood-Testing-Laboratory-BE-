package sum25.group03.patientservice.dtos.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalNoteResponse {
    private Long noteId;
    private Long recordId;
    private Long notedBy;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
