package sum25.group03.iamservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Builder


@Data

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String identityNumber;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private Set<String> roles;
}