package sum25.group03.iamservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Builder


@Data

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String gender;
    private String dateOfBirth;
    private String phoneNumber;
    private String identityNumber;
    private String address;
    private Set<String> roles;
}