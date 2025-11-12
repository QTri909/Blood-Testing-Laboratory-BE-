package sum25.group03.iamservice.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.dto.request.UserUpdateRequest;
import sum25.group03.iamservice.dto.response.UserResponse;
import sum25.group03.iamservice.entity.Role;
import sum25.group03.iamservice.entity.User;
import sum25.group03.iamservice.entity.UserRole;
import sum25.group03.iamservice.event.UserCreatedEvent;
import sum25.group03.iamservice.event.UserDeletedEvent;
import sum25.group03.iamservice.event.UserUpdatedEvent;
import sum25.group03.iamservice.repository.RoleRepository;
import sum25.group03.iamservice.repository.UserRepository;
import sum25.group03.iamservice.repository.UserRoleRepository;
import sum25.group03.iamservice.service.Interface.AuditLogService;
import sum25.group03.iamservice.service.Interface.CognitoService;
import sum25.group03.iamservice.service.Interface.UserService;
import sum25.group03.iamservice.service.KafkaProducerService;

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
        for (String code : request.getRoleCodes()) {
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

            kafkaProducerService.sendUserCreated(event);
        } catch (Exception ignored) {}


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
        if (request.getIdentityNumber() != null) user.setIdentityNumber(request.getIdentityNumber());

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()){

            userRoleRepository.deleteByUserId(user.getId());


            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            List<UserRole> userRoles = roles.stream()
                    .map(role -> new UserRole(null, user, role))
                    .collect(Collectors.toList());

            userRoleRepository.saveAll(userRoles);
            user.setUserRoles(new HashSet<>(userRoles));
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
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, usersPage.getTotalElements());
    }



        @Override
        public Map<String, List<String>> getRolesAndPrivilegesByEmail(String email) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));

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

}
