package sum25.group03.iamservice.event;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String identityNumber;
    private String address;
    private Set<String> roles;
    private Set<String> privileges;
}
