package sum25.group03.iamservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import sum25.group03.iamservice.entity.Role;
import sum25.group03.iamservice.entity.User;
import sum25.group03.iamservice.entity.UserPrivilege;
import sum25.group03.iamservice.repository.UserRepository;
import sum25.group03.iamservice.service.Interface.UserPermissionService;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserRepository userRepository;

    @Override
    public Set<GrantedAuthority> getAuthoritiesByCognitoUserId(String cognitoUserId) {
        User user = userRepository.findByCognitoUserId(cognitoUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<GrantedAuthority> authorities = new HashSet<>();

        user.getUserRoles().forEach(ur -> {
            Role role = ur.getRole();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));

            role.getRolePrivileges().forEach(rp ->
                    authorities.add(new SimpleGrantedAuthority(rp.getPrivilege().getPrivilegeCode()))
            );
        });

        // Load privileges gán trực tiếp cho user
        user.getUserPrivileges().stream()
                .filter(UserPrivilege::getIsActive)
                .forEach(up ->
                        authorities.add(new SimpleGrantedAuthority(up.getPrivilege().getPrivilegeCode()))
                );

        return authorities;
    }
}