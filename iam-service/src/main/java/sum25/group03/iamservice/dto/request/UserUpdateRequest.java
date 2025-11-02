package sum25.group03.iamservice.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String identityNumber;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private List<Long> roleIds;
}