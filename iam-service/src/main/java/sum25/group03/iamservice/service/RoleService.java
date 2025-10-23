package sum25.group03.iamservice.service;

import sum25.group03.iamservice.dto.request.RoleCreateRequest;
import sum25.group03.iamservice.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleCreateRequest request);
    RoleResponse updateRolePermissions(Long roleId, List<Long> privilegeIds);
    void cascadeRolePermissionChanges(Long roleId);
    void deleteRoleIfUnused(Long roleId);
}
