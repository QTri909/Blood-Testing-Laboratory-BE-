package sum25.group03.iamservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.iamservice.dto.request.RoleCreateRequest;
import sum25.group03.iamservice.dto.response.RoleResponse;
import sum25.group03.iamservice.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleCreateRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_ASSIGN_PRIVILEGE')")
    @PutMapping("/{id}/permissions")
    public ResponseEntity<RoleResponse> updateRolePermissions(
            @PathVariable Long id,
            @RequestBody List<Long> privilegeIds) {
        return ResponseEntity.ok(roleService.updateRolePermissions(id, privilegeIds));
    }

    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIfUnused(@PathVariable Long id) {
        roleService.deleteRoleIfUnused(id);
        return ResponseEntity.ok("Role deleted successfully if unused");
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}
