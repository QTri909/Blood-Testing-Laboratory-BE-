package sum25.group03.common.response.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sum25.group03.common.response.services.interfaces.CommonRedisService;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisTokenCheckFilter extends OncePerRequestFilter {

    private final CommonRedisService commonRedisService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Skip if user is not authenticated (public endpoints)
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Only process JWT-based authentication
        if (!(authentication.getPrincipal() instanceof Jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String requestAccessToken = jwt.getTokenValue();
        String requestCognitoSub = jwt.getClaim("sub");

        try {
            // Check Redis for the current active token
            String currentAccessToken = commonRedisService.getValue(requestCognitoSub);

            // Token mismatch → kick out
            log.info("-------");
            log.info("Request token: {}", requestAccessToken);
            log.info("Current token from Redis: {}", currentAccessToken);
            log.info("Out-side Session kickout for user {}", requestCognitoSub);
            if (currentAccessToken != null && !requestAccessToken.equals(currentAccessToken)) {

                log.info("In-side Session kickout for user {}", requestCognitoSub);

                // Send custom response for front-end to detect
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                String json = String.format("{\"code\":\"session_kickout\",\"reason\":\"Token revoked due to login from another device\"}");
                response.getWriter().flush();
                log.info("-------");
                return; // stop filter chain
            }

            // If Redis is null (user first login or no stored token), allow request
        } catch (Exception e) {
            // Redis is down → fail-open, allow login on multiple devices
            // Optional: log warning for monitoring
            log.warn("Redis unavailable, skipping session check for user {}", requestCognitoSub, e);
        }

        // Token matches or Redis is down → continue filter chain
        filterChain.doFilter(request, response);
    }
}

