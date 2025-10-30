package sum25.group03.iamservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.iamservice.dto.request.RoleCreateRequest;
import sum25.group03.iamservice.dto.response.RoleResponse;
import sum25.group03.iamservice.entity.*;
import sum25.group03.iamservice.repository.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {
    private final AuditLogService auditLogService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;
    private final UserPrivilegeRepository userPrivilegeRepository;

    @Override
    public RoleResponse createRole(RoleCreateRequest request) {


        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new RuntimeException("Role name already exists: " + request.getRoleName());
        }

        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new RuntimeException("Role code already exists: " + request.getRoleCode());
        }


        Role role = Role.builder()
                .roleName(request.getRoleName())
                .roleCode(request.getRoleCode())
                .roleDescription(request.getRoleDescription())
                .build();

        role = roleRepository.save(role);


        Set<RolePrivilege> rolePrivileges = new HashSet<>();
        if (request.getPrivilegeIds() != null && !request.getPrivilegeIds().isEmpty()) {
            List<Privilege> privileges = privilegeRepository.findAllById(request.getPrivilegeIds());

            for (Privilege p : privileges) {
                RolePrivilege rp = RolePrivilege.builder()
                        .role(role)
                        .privilege(p)
                        .build();
                rolePrivileges.add(rp);
            }

            rolePrivilegeRepository.saveAll(rolePrivileges);
            role.setRolePrivileges(rolePrivileges);

            auditLogService.record(
                    "CREATE",
                    "Role",
                    role.getId(),
                    "system",
                    "Created role: " + role.getRoleCode()
            );

        }


        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .roleDescription(role.getRoleDescription())
                .privileges(role.getRolePrivileges().stream()
                        .map(rp -> rp.getPrivilege().getPrivilegeName())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public RoleResponse updateRolePermissions(Long roleId, List<Long> privilegeIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));


        rolePrivilegeRepository.deleteByRoleId(roleId);


        List<RolePrivilege> rolePrivileges = privilegeIds.stream()
                .map(pid -> {
                    Privilege p = privilegeRepository.findById(pid)
                            .orElseThrow(() -> new RuntimeException("Privilege not found with id: " + pid));
                    return RolePrivilege.builder()
                            .role(role)
                            .privilege(p)
                            .build();
                }).collect(Collectors.toList());

        rolePrivilegeRepository.saveAll(rolePrivileges);

        auditLogService.record(
                "UPDATE",
                "Role",
                role.getId(),
                "system",
                "Updated privileges for role: " + role.getRoleCode()
        );



        cascadeRolePermissionChanges(roleId);

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .roleDescription(role.getRoleDescription())
                .privileges(
                        rolePrivileges.stream().map(rp -> rp.getPrivilege().getPrivilegeName()).collect(Collectors.toSet())
                )
                .build();
    }

    @Override
    public void cascadeRolePermissionChanges(Long roleId) {
        List<UserRole> userRoles = userRoleRepository.findByRoleId(roleId);

        // Lấy các quyền mới của role
        List<Privilege> privileges = privilegeRepository.findByRoleId(roleId);

        for (UserRole ur : userRoles) {
            userPrivilegeRepository.deleteByUserId(ur.getUser().getId());

            // Cấp lại quyền tương ứng
            List<UserPrivilege> ups = privileges.stream()
                    .map(p -> UserPrivilege.builder()
                            .user(ur.getUser())
                            .privilege(p)
                            .isActive(true)
                            .build())
                    .collect(Collectors.toList());

            userPrivilegeRepository.saveAll(ups);


        }
        auditLogService.record(
                "SYSTEM_SYNC",
                "UserPrivilege",
                null,
                "system",
                "Synchronized privileges for users assigned to roleId=" + roleId
        );
    }

    @Override
    public void deleteRoleIfUnused(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        if (List.of("ADMIN", "SYSTEM", "SUPERUSER").contains(role.getRoleCode().toUpperCase())) {
            throw new RuntimeException("Cannot delete system-critical role: " + role.getRoleCode());
        }

        if (!role.getUserRoles().isEmpty()) {
            throw new RuntimeException("Cannot delete role assigned to users");
        }

        rolePrivilegeRepository.deleteByRoleId(roleId);
        roleRepository.delete(role);

        auditLogService.record(
                "DELETE",
                "Role",
                role.getId(),
                "system",
                "Deleted role: " + role.getRoleCode()
        );


    }
}
