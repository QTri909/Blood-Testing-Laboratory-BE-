package sum25.group03.iamservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.iamservice.entity.Role;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterSearchingRequest {
    private String fullName;
    private String identityNumber;
    private String phoneNumber;
    private String email;
    private String roleCode;

    // paging
    private int page = 0;
    private int size = 10;
}