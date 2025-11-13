package sum25.group03.iamservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.iamservice.dto.request.RoleCreateRequest;
import sum25.group03.iamservice.dto.response.RoleResponse;
import sum25.group03.iamservice.entity.*;
import sum25.group03.iamservice.event.RoleCreatedEvent;
import sum25.group03.iamservice.event.RoleDeletedEvent;
import sum25.group03.iamservice.event.RoleUpdatedEvent;
import sum25.group03.iamservice.repository.*;
import sum25.group03.iamservice.service.Interface.AuditLogService;
import sum25.group03.iamservice.service.Interface.RoleService;
import sum25.group03.iamservice.service.KafkaProducerService;

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
    private final KafkaProducerService kafkaProducerService;

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

            try {
                RoleCreatedEvent event = RoleCreatedEvent.builder()
                        .Id(role.getId())
                        .roleCode(role.getRoleCode())
                        .roleName(role.getRoleName())
                        .roleDescription(role.getRoleDescription())
                        .privileges(
                                role.getRolePrivileges().stream()
                                        .map(rp -> rp.getPrivilege().getPrivilegeName())
                                        .collect(Collectors.toSet())
                        )
                        .build();
                kafkaProducerService.sendRoleCreated(event);
            } catch (Exception ignored) {
            }

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

        try {
            RoleUpdatedEvent event = RoleUpdatedEvent.builder()
                    .Id(role.getId())
                    .roleCode(role.getRoleCode())
                    .roleName(role.getRoleName())
                    .roleDescription(role.getRoleDescription())
                    .privileges(
                            rolePrivileges.stream()
                                    .map(rp -> rp.getPrivilege().getPrivilegeName())
                                    .collect(Collectors.toSet())
                    )
                    .build();
            kafkaProducerService.sendRoleUpdated(event);
        } catch (Exception ignored) {
        }



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


        List<Privilege> privileges = privilegeRepository.findByRoleId(roleId);

        for (UserRole ur : userRoles) {
            userPrivilegeRepository.deleteByUserId(ur.getUser().getId());


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
        try {
            RoleDeletedEvent event = RoleDeletedEvent.builder()
                    .Id(role.getId())
                    .roleCode(role.getRoleCode())
                    .roleName(role.getRoleName())
                    .roleDescription(role.getRoleDescription())
                    .build();
            kafkaProducerService.sendRoleDeleted(event);
        } catch (Exception ignored) {
        }

    }

    @Override
    public Page<RoleResponse> getAllRoles(Pageable pageable) {

        Page<Role> roles = roleRepository.findAll(pageable);

        return roles.map(role ->
                RoleResponse.builder()
                        .id(role.getId())
                        .roleName(role.getRoleName())
                        .roleCode(role.getRoleCode())
                        .roleDescription(role.getRoleDescription())
                        .privileges(
                                role.getRolePrivileges().stream()
                                        .map(rp -> rp.getPrivilege().getPrivilegeName())
                                        .collect(Collectors.toSet())
                        )
                        .build()
        );
    }
}
