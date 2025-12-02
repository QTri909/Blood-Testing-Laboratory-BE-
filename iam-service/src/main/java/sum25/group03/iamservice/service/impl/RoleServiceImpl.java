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

        boolean changed = false;

        // =================== Update basic fields ===================
        if (request.getRoleName() != null && !request.getRoleName().equals(role.getRoleName())) {
            role.setRoleName(request.getRoleName());
            changed = true;
        }

        if (request.getRoleCode() != null && !request.getRoleCode().equals(role.getRoleCode())) {
            boolean exists = roleRepository.existsByRoleCodeAndIdNot(request.getRoleCode(), roleId);
            if (exists) {
                throw new RuntimeException("Role code already exists: " + request.getRoleCode());
            }
            role.setRoleCode(request.getRoleCode());
            changed = true;
        }

        if (request.getRoleDescription() != null &&
                !request.getRoleDescription().equals(role.getRoleDescription())) {
            role.setRoleDescription(request.getRoleDescription());
            changed = true;
        }

        // =================== Update privileges ===================

        Set<RolePrivilege> currentPrivileges = role.getRolePrivileges();
        Set<Long> oldPrivilegeIds = currentPrivileges.stream()
                .map(rp -> rp.getPrivilege().getId())
                .collect(Collectors.toSet());

        Set<Long> newPrivilegeIds = request.getPrivilegeIds() == null
                ? new HashSet<>()
                : new HashSet<>(request.getPrivilegeIds());

        // Nếu không thay đổi privileges → KHÔNG động vào DB
        boolean privilegesChanged = !oldPrivilegeIds.equals(newPrivilegeIds);

        Set<String> finalPrivileges = new HashSet<>();

        if (privilegesChanged) {
            changed = true;

            currentPrivileges.clear();

            if (!newPrivilegeIds.isEmpty()) {
                List<Privilege> privileges = privilegeRepository.findAllById(newPrivilegeIds);

                for (Privilege p : privileges) {
                    currentPrivileges.add(
                            RolePrivilege.builder()
                                    .role(role)
                                    .privilege(p)
                                    .build()
                    );
                }

                finalPrivileges = privileges.stream()
                        .map(Privilege::getPrivilegeName)
                        .collect(Collectors.toSet());
            }
        } else {
            // Không thay đổi → build lại từ dữ liệu cũ
            finalPrivileges = currentPrivileges.stream()
                    .map(rp -> rp.getPrivilege().getPrivilegeName())
                    .collect(Collectors.toSet());
        }

        // =================== Nếu không thay đổi gì → return luôn ===================
        if (!changed) {
            return RoleResponse.builder()
                    .id(role.getId())
                    .roleName(role.getRoleName())
                    .roleCode(role.getRoleCode())
                    .roleDescription(role.getRoleDescription())
                    .privileges(finalPrivileges)
                    .build();
        }

        // =================== Save ===================
        roleRepository.save(role);

        // =================== Audit Log ===================
        auditLogService.record(
                "UPDATE",
                "Role",
                role.getId(),
                "system",
                "Updated role info + privileges: " + role.getRoleCode()
        );

        // =================== Kafka Events ===================
        try {
            RoleUpdatedEvent event = RoleUpdatedEvent.builder()
                    .Id(role.getId())
                    .roleCode(role.getRoleCode())
                    .roleName(role.getRoleName())
                    .roleDescription(role.getRoleDescription())
                    .privileges(finalPrivileges)
                    .build();

            kafkaProducerService.sendRoleUpdated(event);
        } catch (Exception ignored) {}

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
    @Transactional
    public void cascadeRolePermissionChanges(Long roleId) {

        List<UserRole> userRoles = userRoleRepository.findByRoleId(roleId);

        List<Privilege> newPrivileges = privilegeRepository.findByRoleId(roleId);

        for (UserRole ur : userRoles) {

            User user = ur.getUser();

            List<UserPrivilege> existingList = userPrivilegeRepository.findByUserId(user.getId());
            Set<UserPrivilege> existing = new HashSet<>(existingList);


            Set<Long> existingIds = existing.stream()
                    .map(up -> up.getPrivilege().getId())
                    .collect(Collectors.toSet());

            Set<Long> newIds = newPrivileges.stream()
                    .map(Privilege::getId)
                    .collect(Collectors.toSet());

            List<Privilege> toAdd = newPrivileges.stream()
                    .filter(p -> !existingIds.contains(p.getId()))
                    .toList();

            List<UserPrivilege> toRemove = existing.stream()
                    .filter(up -> !newIds.contains(up.getPrivilege().getId()))
                    .toList();

            if (!toRemove.isEmpty()) {
                userPrivilegeRepository.deleteAll(toRemove);
            }

            // ADD chỉ những cái mới
            if (!toAdd.isEmpty()) {
                List<UserPrivilege> newUserPrivs = toAdd.stream()
                        .map(p -> UserPrivilege.builder()
                                .user(user)
                                .privilege(p)
                                .isActive(true)
                                .build())
                        .toList();

                userPrivilegeRepository.saveAll(newUserPrivs);
            }
        }

        auditLogService.record(
                "SYSTEM_SYNC",
                "UserPrivilege",
                null,
                "system",
                "Synced privilege changes for roleId=" + roleId
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

    @Override
    @Transactional(readOnly = true)
    public Set<String> getPrivilegesByRoleId(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        return role.getRolePrivileges()
                .stream()
                .map(rp -> rp.getPrivilege().getPrivilegeName())
                .collect(Collectors.toSet());
    }
}
