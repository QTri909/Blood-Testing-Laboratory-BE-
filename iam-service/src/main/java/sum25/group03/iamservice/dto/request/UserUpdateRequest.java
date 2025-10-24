package sum25.group03.iamservice.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String phone;
    private Boolean isActive;
    private List<Long> roleIds;
}