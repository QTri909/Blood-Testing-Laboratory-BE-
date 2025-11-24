package sum25.group03.iamservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.iamservice.dto.request.RoleCreateRequest;
import sum25.group03.iamservice.dto.request.RoleUpdateRequest;
import sum25.group03.iamservice.dto.response.PrivilegeResponse;
import sum25.group03.iamservice.dto.response.RoleResponse;
import sum25.group03.iamservice.service.Interface.PrivilegeService;
import sum25.group03.iamservice.service.Interface.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final PrivilegeService privilegeService;


    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleCreateRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ApiResponse.data(response)
                .message("Role created successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RoleResponse> updateRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest request) {

        RoleResponse updatedRole = roleService.updateRole(id, request);

        return ApiResponse.data(updatedRole)
                .message("Role updated successfully")
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
    public ApiResponse<Page<RoleResponse>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleResponse> result = roleService.getAllRoles(pageable);

        return ApiResponse.data(result)
                .message("Roles retrieved successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('PRIVILEGE_VIEW')")
    @GetMapping("/privileges")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<PrivilegeResponse>> getAllPrivileges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "99") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PrivilegeResponse> data = privilegeService.getAllPrivileges(pageable);

        return ApiResponse.data(data)
                .message("Privileges retrieved successfully")
                .build();
    }
}
