package sum25.group03.patientservice.dtos.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class UserSnapshotRequest {
    private Long externalUserId;
}
