package sum25.group03.iamservice.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.common.response.events.UserCreatedEvent;
import sum25.group03.iamservice.dto.request.UserFilterSearchingRequest;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.dto.request.UserUpdateRequest;
import sum25.group03.iamservice.dto.response.UserResponse;
import sum25.group03.iamservice.entity.PendingUser;
import sum25.group03.iamservice.entity.Role;
import sum25.group03.iamservice.entity.User;
import sum25.group03.iamservice.entity.UserRole;
import sum25.group03.iamservice.event.MonitoringLogEvent;
import sum25.group03.iamservice.event.UserDeletedEvent;
import sum25.group03.iamservice.event.UserUpdatedEvent;
import sum25.group03.iamservice.repository.PendingUserRepository;
import sum25.group03.iamservice.repository.RoleRepository;
import sum25.group03.iamservice.repository.UserRepository;
import sum25.group03.iamservice.repository.UserRoleRepository;
import sum25.group03.iamservice.service.Interface.AuditLogService;
import sum25.group03.iamservice.service.Interface.CognitoService;
import sum25.group03.iamservice.service.Interface.UserService;
import sum25.group03.iamservice.service.KafkaProducerService;
import sum25.group03.iamservice.specification.UserSpecification;

import java.util.*;

import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final AuditLogService auditLogService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CognitoService cognitoService;
    private final KafkaProducerService kafkaProducerService;
    private final PendingUserRepository pendingUserRepository;


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
    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        if (userRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new RuntimeException("Identity number already exists!");
        }


        String cognitoUserId = cognitoService.createUser(request);


        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .identityNumber(request.getIdentityNumber())
                .address(request.getAddress())
                .cognitoUserId(cognitoUserId)
                .accountNonLocked(false)
                .build();

        user = userRepository.save(user);


        Set<UserRole> userRoles = new HashSet<>();
        if (request.getRoleCode() != null && !request.getRoleCode().isBlank()) {

            String code = request.getRoleCode();

            Role role = roleRepository.findByRoleCode(code)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + code));

            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

            userRoles.add(userRole);
        }

        userRoleRepository.saveAll(userRoles);
        user.setUserRoles(userRoles);


        auditLogService.record(
                "CREATE",
                "User",
                user.getId(),
                "system",
                "Created new user with email: " + user.getEmail()
        );
        try {
            UserCreatedEvent event = UserCreatedEvent.builder()
                    .id(user.getId() + "")
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .gender(user.getGender())
                    .dateOfBirth(user.getDateOfBirth())
                    .identityNumber(user.getIdentityNumber())
                    .address(user.getAddress())
                    .roles(
                            user.getUserRoles().stream()
                                    .map(ur -> ur.getRole().getRoleCode())
                                    .collect(Collectors.toSet())
                    )
                    .privileges(
                            user.getUserRoles().stream()
                                    .flatMap(ur -> ur.getRole().getRolePrivileges().stream())
                                    .map(rp -> rp.getPrivilege().getPrivilegeCode())
                                    .collect(Collectors.toSet())
                    )
                    .build();

            kafkaProducerService.sendUserCreated(event);
        } catch (Exception ignored) {}

        Long operatorId = getOperatorDatabaseId();

        MonitoringLogEvent log = MonitoringLogEvent.builder()
                .action("CREATE_USER")
                .operator(operatorId == null ? "system" : operatorId.toString())
                .message("Created new user with id: " + user.getId())
                .sourceService("IAM-Service")
                .data(Map.of(
                        "userId", user.getId(),
                        "email", user.getEmail(),
                        "roles", user.getUserRoles().stream()
                                .map(ur -> ur.getRole().getRoleCode())
                                .toList()
                ))
                .build();

        kafkaProducerService.sendMonitoringLog(log);


        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setGender(user.getGender());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setIdentityNumber(user.getIdentityNumber());
        response.setRoles(
                user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getRoleCode())
                        .collect(Collectors.toSet())
        );

        return response;
    }


    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));


        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getIdentityNumber() != null) user.setIdentityNumber(request.getIdentityNumber());

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            user.getUserRoles().clear();
            Role role = roleRepository.findById(request.getRoleIds().get(0))
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.getUserRoles().add(new UserRole(null, user, role));
        }

        userRepository.save(user);

        cognitoService.updateUserAttributes(user);

        auditLogService.record(
                "UPDATE",
                "User",
                user.getId(),
                "system",
                "Updated user info for: " + user.getEmail()
        );

        try {
            UserUpdatedEvent event = UserUpdatedEvent.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .gender(user.getGender())
                    .dateOfBirth(user.getDateOfBirth())
                    .identityNumber(user.getIdentityNumber())
                    .address(user.getAddress())
                    .roles(
                            user.getUserRoles().stream()
                                    .map(ur -> ur.getRole().getRoleCode())
                                    .collect(Collectors.toSet())
                    )
                    .privileges(
                            user.getUserRoles().stream()
                                    .flatMap(ur -> ur.getRole().getRolePrivileges().stream())
                                    .map(rp -> rp.getPrivilege().getPrivilegeCode())
                                    .collect(Collectors.toSet())
                    )
                    .build();

            kafkaProducerService.sendUserUpdated(event);
        } catch (Exception ignored) {}

        Long operatorId = getOperatorDatabaseId();

        MonitoringLogEvent log = MonitoringLogEvent.builder()
                .action("UPDATE_USER")
                .operator(operatorId == null ? "system" : operatorId.toString())
                .message("Updated user with id: " + user.getId())
                .sourceService("IAM-Service")
                .data(Map.of(
                        "userId", user.getId(),
                        "updatedFields", request
                ))
                .build();

        kafkaProducerService.sendMonitoringLog(log);




        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .identityNumber(user.getIdentityNumber())
                .address(user.getAddress())
                .roles(user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getRoleName())
                        .collect(Collectors.toSet()))
                .build();


    }

    @Transactional
    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setIsActive(false);
        userRepository.save(user);

        cognitoService.disableUser(user.getEmail());

        auditLogService.record(
                "DEACTIVATE",
                "User",
                user.getId(),
                "system",
                "Deactivated user account: " + user.getEmail()
        );
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));



        userRoleRepository.deleteByUserId(user.getId());
        userRepository.delete(user);

        cognitoService.deleteUser(user.getEmail());

        auditLogService.record(
                "DELETE",
                "User",
                user.getId(),
                "system",
                "Deleted user with email: " + user.getEmail()
        );
        try {
            UserDeletedEvent event = UserDeletedEvent.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .build();

            kafkaProducerService.sendUserDeleted(event);
        } catch (Exception ignored) {}

        Long operatorId = getOperatorDatabaseId();

        MonitoringLogEvent log = MonitoringLogEvent.builder()
                .action("DELETE_USER")
                .operator(operatorId == null ? "system" : operatorId.toString())
                .message("Deleted user with id: " + user.getId())
                .sourceService("IAM-Service")
                .data(Map.of(
                        "userId", user.getId(),
                        "email", user.getEmail()
                ))
                .build();

        kafkaProducerService.sendMonitoringLog(log);

    }

    @Override
    public Page<UserResponse> getAllPatients(Pageable pageable) {
        Page<User> usersPage = userRepository.findByRoleCode("PATIENT", pageable);

        List<UserResponse> responses = usersPage.getContent().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .address(user.getAddress())
                        .gender(user.getGender())
                        .dateOfBirth(user.getDateOfBirth())
                        .identityNumber(user.getIdentityNumber())
                        .roles(
                                user.getUserRoles().stream()
                                        .map(ur -> ur.getRole().getRoleCode())
                                        .collect(Collectors.toSet())
                        )
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, usersPage.getTotalElements());
    }



        @Override
        public Map<String, List<String>> getRolesAndPrivilegesByUsername(String username) {
            User user = userRepository.findByEmailOrIdentityNumber(username, username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // Lấy role
            List<String> roles = user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getRoleCode())
                    .toList();

            // Lấy privilege
            List<String> privileges = user.getUserRoles().stream()
                    .flatMap(ur -> ur.getRole().getRolePrivileges().stream())
                    .map(rp -> rp.getPrivilege().getPrivilegeCode())
                    .distinct()
                    .toList();

            Map<String, List<String>> result = new HashMap<>();
            result.put("roles", roles);
            result.put("privileges", privileges);

            return result;
        }


    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserResponse> responses = usersPage.getContent().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .address(user.getAddress())
                        .gender(user.getGender())
                        .dateOfBirth(user.getDateOfBirth())
                        .identityNumber(user.getIdentityNumber())
                        .roles(
                                user.getUserRoles()
                                        .stream()
                                        .map(ur -> ur.getRole().getRoleCode())
                                        .collect(Collectors.toSet())
                        )
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, usersPage.getTotalElements());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .identityNumber(user.getIdentityNumber())
                .roles(user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getRoleName())
                        .collect(Collectors.toSet()))
                .build();
        return response;
    }

    @Override
    public UserResponse getUserByIdentityNumber(String identityNumber) {
        User user = userRepository.findByIdentityNumber(identityNumber)
                .orElseThrow(() -> new RuntimeException("User not found with identityNumber: " + identityNumber));

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .identityNumber(user.getIdentityNumber())
                .roles(user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getRoleName())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional
    public String approvePendingUser(Long id) {
        PendingUser pending = pendingUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pending user not found"));

        if (pending.isApproved()) {
            throw new RuntimeException("This pending user was already approved!");
        }

        // Chuyển đổi PendingUser → UserCreateRequest
        UserCreateRequest request = new UserCreateRequest();
        request.setFullName(pending.getFullName());
        request.setEmail(pending.getEmail());
        request.setPhoneNumber(pending.getPhoneNumber());
        request.setGender(pending.getGender());
        request.setDateOfBirth(pending.getDateOfBirth());
        request.setIdentityNumber(pending.getIdentityNumber());
        request.setAddress(pending.getAddress());
        request.setRoleCode(pending.getRoleCode());

        createUser(request);

        List<PendingUser> all = pendingUserRepository.findByEmail(pending.getEmail());

        for (PendingUser p : all) {
            p.setApproved(true);
        }

        pendingUserRepository.saveAll(all);

        // Ghi nhật ký duyệt user
        auditLogService.record(
                "APPROVE",
                "PendingUser",
                pending.getId(),
                "system",
                "Approved pending user and created account: " + pending.getEmail()
        );

        return "User created successfully from pending list";
    }

    @Override
    public List<PendingUser> getPendingUsers() {
        return pendingUserRepository.findByApprovedFalse();
    }


    public Page<UserResponse> searchFilteredUsers(UserFilterSearchingRequest request) {
        var spec = UserSpecification.buildFromRequest(request);
        var pageable = PageRequest.of(Math.max(0, request.getPage()), Math.max(1, request.getSize()));
        Page<User> users = userRepository.findAll(spec, pageable);

        // Map User -> UserResponse. Adjust mapping according to your UserResponse fields.
        return users.map(this::toUserResponse);
    }

    private UserResponse toUserResponse(User u) {
        UserResponse resp = new UserResponse();
        // adjust field names to match entities and response DTO
        resp.setId(u.getId());
        resp.setFullName(u.getFullName());
        resp.setIdentityNumber(u.getIdentityNumber());
        resp.setEmail(u.getEmail());
        resp.setPhoneNumber(u.getPhoneNumber());
        resp.setGender(u.getGender());
        resp.setDateOfBirth(u.getDateOfBirth());
        resp.setAddress(u.getAddress());
        Set<String> roles = u.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleCode())
                .collect(Collectors.toSet());
        resp.setRoles(roles);
        return resp;
    }



}
