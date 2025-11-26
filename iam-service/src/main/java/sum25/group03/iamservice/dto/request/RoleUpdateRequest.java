package sum25.group03.iamservice.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RoleUpdateRequest {

    private String roleName;
    private String roleCode;
    private String roleDescription;

    private List<Long> privilegeIds;
}
