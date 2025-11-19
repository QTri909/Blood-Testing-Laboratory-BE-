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
    private String identityNumber;
    private String address;
    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private List<String> roles;
    private LocalDateTime lastUpdated;
}