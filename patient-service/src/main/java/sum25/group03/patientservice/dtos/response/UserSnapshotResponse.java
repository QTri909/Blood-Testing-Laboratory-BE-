package sum25.group03.patientservice.dtos.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSnapshotResponse {
    private Long id;
    private Long externalUserId;
    private LocalDateTime lastUpdated;
}
