package sum25.group03.payment_service.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Dev Security Config cho payment-service
 * - Tạm thời tắt AWS Cognito JWT validation
 * - Cho phép tất cả request (không còn 401)
 * - Chỉ dùng cho môi trường dev / test
 */
//@Configuration
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Tắt CSRF để test POST từ Postman
                .csrf(csrf -> csrf.disable())

                // Cho phép tất cả request không cần auth
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}