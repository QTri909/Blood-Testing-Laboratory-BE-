package sum25.group03.patientservice.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.patientservice.enums.MedicalRecordStatus;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilteredMedicalRecordRequest {

    private Long patientId;
    private Set<MedicalRecordStatus> statusList;

    private Integer page = 0;
    private Integer size = 10;
}
