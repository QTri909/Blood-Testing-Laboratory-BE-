package sum25.group03.iamservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.iamservice.dto.request.RoleCreateRequest;
import sum25.group03.iamservice.dto.response.RoleResponse;
import sum25.group03.iamservice.service.Interface.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleCreateRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ApiResponse.data(response)
                .message("Role created successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ASSIGN_PRIVILEGE')")
    @PutMapping("/{id}/permissions")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RoleResponse> updateRolePermissions(
            @PathVariable Long id,
            @RequestBody List<Long> privilegeIds) {

        RoleResponse updatedRole = roleService.updateRolePermissions(id, privilegeIds);
        return ApiResponse.data(updatedRole)
                .message("Role privileges updated successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<String> deleteIfUnused(@PathVariable Long id) {
        roleService.deleteRoleIfUnused(id);
        return ApiResponse.data("Role deleted successfully if unused")
                .message("Deletion completed")
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ApiResponse.data(roles)
                .message("Roles retrieved successfully")
                .build();
    }
}
