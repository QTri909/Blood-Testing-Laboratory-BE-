package sum25.group03.iamservice.dto.response;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String identityNumber;
    private String address;
    private Set<String> roles;
}