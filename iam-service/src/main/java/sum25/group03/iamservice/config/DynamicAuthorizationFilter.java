    package sum25.group03.iamservice.config;

    import com.auth0.jwt.JWT;
    import com.auth0.jwt.interfaces.DecodedJWT;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;
    import sum25.group03.iamservice.service.UserPermissionService;

    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import java.io.IOException;
    import java.util.Set;

    @Component
    @RequiredArgsConstructor
    public class DynamicAuthorizationFilter extends OncePerRequestFilter {

        private final UserPermissionService userPermissionService;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {


            String path = request.getRequestURI();


            if (path.startsWith("/auth/login") ||
                    path.startsWith("/auth/refresh") ||
                    path.startsWith("/auth/privileges")) {
                filterChain.doFilter(request, response);
                return;
            }

            String cognitoUserId = extractCognitoUserId(request);

            if (cognitoUserId != null) {
                Set<GrantedAuthority> authorities = userPermissionService.getAuthoritiesByCognitoUserId(cognitoUserId);

                String requiredPrivilege = mapPathToPrivilege(request.getRequestURI(), request.getMethod());

                boolean hasPermission = authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals(requiredPrivilege) || a.getAuthority().startsWith("ROLE_"));

                if (!hasPermission) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        }

        private String extractCognitoUserId(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                DecodedJWT jwt = JWT.decode(token);
                return jwt.getSubject(); // claim "sub" từ Cognito
            }
            return null;
        }

        private String mapPathToPrivilege(String path, String method) {
            // TODO: sau này có thể replace bằng DB api_privileges mapping
            return switch (path) {
                case "/admin/dashboard" -> "VIEW_DASHBOARD";
                case "/lab/data" -> "VIEW_LAB_DATA";
                case "/doctor/patient" -> "VIEW_PATIENT";
                default -> "";
            };
        }
    }
