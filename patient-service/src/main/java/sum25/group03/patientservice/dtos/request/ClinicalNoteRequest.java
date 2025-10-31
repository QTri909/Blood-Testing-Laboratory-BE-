package sum25.group03.patientservice.dtos.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalNoteRequest {
    private Long recordId;
    private Long notedBy;
    private String note;
}
