package sum25.group03.patientservice.feign.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String identityNumber;
    private String address;
    private String gender;
    private String dateOfBirth;
    private List<String> roles;
}
