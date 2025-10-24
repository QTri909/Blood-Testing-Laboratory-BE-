package sum25.group03.iamservice.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public interface UserPermissionService {
    /**
     * Load tất cả quyền của user từ DB (roles + role privileges + user privileges)
     * @param cognitoUserId từ JWT Cognito
     * @return Set<GrantedAuthority> dùng để Spring Security check quyền
     */
    Set<GrantedAuthority> getAuthoritiesByCognitoUserId(String cognitoUserId);
}
