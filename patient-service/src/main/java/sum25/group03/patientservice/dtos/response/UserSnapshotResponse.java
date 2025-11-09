package sum25.group03.patientservice.dtos.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSnapshotResponse {
    private Long id;
    private Long externalUserId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<String> roles;
    private LocalDateTime lastUpdated;
}