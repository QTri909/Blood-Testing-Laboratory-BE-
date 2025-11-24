package sum25.group03.iamservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.iamservice.dto.request.RoleCreateRequest;
import sum25.group03.iamservice.dto.request.RoleUpdateRequest;
import sum25.group03.iamservice.dto.response.RoleResponse;
import sum25.group03.iamservice.entity.*;
import sum25.group03.iamservice.event.MonitoringLogEvent;
import sum25.group03.iamservice.event.RoleCreatedEvent;
import sum25.group03.iamservice.event.RoleDeletedEvent;
import sum25.group03.iamservice.event.RoleUpdatedEvent;
import sum25.group03.iamservice.repository.*;
import sum25.group03.iamservice.service.Interface.AuditLogService;
import sum25.group03.iamservice.service.Interface.RoleService;
import sum25.group03.iamservice.service.KafkaProducerService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final UserRepository userRepository;


    private Long getOperatorDatabaseId() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) return null;

            Object principal = authentication.getPrincipal();

            if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
                String cognitoUserId = jwt.getSubject();

                return userRepository.findByCognitoUserId(cognitoUserId)
                        .map(User::getId)
                        .orElse(null);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

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

            Long operatorId = getOperatorDatabaseId();

            MonitoringLogEvent log = MonitoringLogEvent.builder()
                    .action("CREATE_ROLE")
                    .operator(operatorId == null ? "system" : operatorId.toString())
                    .message("Created role: " + role.getRoleCode())
                    .sourceService("IAM-Service")
                    .data(Map.of(
                            "roleId", role.getId(),
                            "roleCode", role.getRoleCode(),
                            "privileges", role.getRolePrivileges().stream()
                                    .map(rp -> rp.getPrivilege().getPrivilegeName())
                                    .toList()
                    ))
                    .build();

            kafkaProducerService.sendMonitoringLog(log);

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
    @Transactional
    public RoleResponse updateRole(Long roleId, RoleUpdateRequest request) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        if (request.getRoleName() != null) {
            role.setRoleName(request.getRoleName());
        }

        if (request.getRoleCode() != null) {
            boolean exists = roleRepository.existsByRoleCodeAndIdNot(request.getRoleCode(), roleId);
            if (exists) {
                throw new RuntimeException("Role code already exists: " + request.getRoleCode());
            }
            role.setRoleCode(request.getRoleCode());
        }

        if (request.getRoleDescription() != null) {
            role.setRoleDescription(request.getRoleDescription());
        }

        roleRepository.save(role);

        if (role.getRolePrivileges() == null) {
            role.setRolePrivileges(new HashSet<>());
        } else {
            role.getRolePrivileges().clear();
        }

        Set<String> finalPrivileges = new HashSet<>();
        List<Long> privilegeIds = request.getPrivilegeIds();

        if (privilegeIds != null && !privilegeIds.isEmpty()) {
            List<Privilege> privileges = privilegeRepository.findAllById(privilegeIds);

            List<RolePrivilege> newPrivileges = privileges.stream()
                    .map(p -> RolePrivilege.builder()
                            .role(role)
                            .privilege(p)
                            .build())
                    .toList();

            rolePrivilegeRepository.saveAll(newPrivileges);
            role.getRolePrivileges().addAll(newPrivileges);

            finalPrivileges = privileges.stream()
                    .map(Privilege::getPrivilegeName)
                    .collect(Collectors.toSet());
        }

        auditLogService.record(
                "UPDATE",
                "Role",
                role.getId(),
                "system",
                "Updated role info + privileges: " + role.getRoleCode()
        );

        try {
            RoleUpdatedEvent event = RoleUpdatedEvent.builder()
                    .Id(role.getId())
                    .roleCode(role.getRoleCode())
                    .roleName(role.getRoleName())
                    .roleDescription(role.getRoleDescription())
                    .privileges(finalPrivileges)
                    .build();

            kafkaProducerService.sendRoleUpdated(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long operatorId = getOperatorDatabaseId();

        MonitoringLogEvent log = MonitoringLogEvent.builder()
                .action("UPDATE_ROLE")
                .operator(operatorId == null ? "system" : operatorId.toString())
                .message("Updated role: " + role.getRoleCode())
                .sourceService("IAM-Service")
                .data(Map.of(
                        "roleId", role.getId(),
                        "roleCode", role.getRoleCode(),
                        "roleName", role.getRoleName(),
                        "roleDescription", role.getRoleDescription(),
                        "privileges", finalPrivileges
                ))
                .build();

        kafkaProducerService.sendMonitoringLog(log);

        cascadeRolePermissionChanges(roleId);

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .roleDescription(role.getRoleDescription())
                .privileges(finalPrivileges)
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

        Long operatorId = getOperatorDatabaseId();

        MonitoringLogEvent log = MonitoringLogEvent.builder()
                .action("DELETE_ROLE")
                .operator(operatorId == null ? "system" : operatorId.toString())
                .message("Deleted role: " + role.getRoleCode())
                .sourceService("IAM-Service")
                .data(Map.of(
                        "roleId", role.getId(),
                        "roleCode", role.getRoleCode()
                ))
                .build();

        kafkaProducerService.sendMonitoringLog(log);

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
