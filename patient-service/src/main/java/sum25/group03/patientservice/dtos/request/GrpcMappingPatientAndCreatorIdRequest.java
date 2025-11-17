package sum25.group03.patientservice.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrpcMappingPatientAndCreatorIdRequest {
    private List<Long> patientIds;
    private List<Long> creatorIds;
}
