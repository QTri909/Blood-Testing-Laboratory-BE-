package sum25.group03.iamservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.dto.request.UserUpdateRequest;
import sum25.group03.iamservice.dto.response.UserResponse;
import sum25.group03.iamservice.entity.Role;
import sum25.group03.iamservice.entity.User;
import sum25.group03.iamservice.entity.UserRole;
import sum25.group03.iamservice.repository.RoleRepository;
import sum25.group03.iamservice.repository.UserRepository;
import sum25.group03.iamservice.repository.UserRoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CognitoService cognitoService;

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
                .identityNumber(request.getIdentityNumber())
                .address(request.getAddress())
                .cognitoUserId(cognitoUserId) // lưu Cognito ID
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


        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
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
        if (request.getPhone() != null) user.setPhoneNumber(request.getPhone());


        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {

            userRoleRepository.deleteByUserId(user.getId());


            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            List<UserRole> userRoles = roles.stream()
                    .map(role -> new UserRole(null, user, role))
                    .collect(Collectors.toList());

            userRoleRepository.saveAll(userRoles);
        }

        userRepository.save(user);


        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
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

        cognitoService.disableUser(user.getCognitoUserId());

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        cognitoService.deleteUser(user.getCognitoUserId());
        userRoleRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }
}
