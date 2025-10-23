package sum25.group03.iamservice.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class RoleResponse {
    private Long id;
    private String roleName;
    private String roleCode;
    private String roleDescription;
    private Set<String> privileges; // tên quyền
}
