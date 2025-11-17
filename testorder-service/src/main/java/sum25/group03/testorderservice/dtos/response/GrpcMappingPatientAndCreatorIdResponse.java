package sum25.group03.testorderservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrpcMappingPatientAndCreatorIdResponse {
    private Map<Long, String> mappingPatientIdToName;
    private Map<Long, String> mappingCreatorIdToName;
}
