package sum25.group03.iamservice.service.Interface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.iamservice.dto.request.RoleCreateRequest;
import sum25.group03.iamservice.dto.request.RoleUpdateRequest;
import sum25.group03.iamservice.dto.response.RoleResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleResponse createRole(RoleCreateRequest request);
    RoleResponse updateRole(Long roleId, RoleUpdateRequest request);
    Set<String> getPrivilegesByRoleId(Long roleId);
    void cascadeRolePermissionChanges(Long roleId);
    void deleteRoleIfUnused(Long roleId);
    Page<RoleResponse> getAllRoles(Pageable pageable);
}
