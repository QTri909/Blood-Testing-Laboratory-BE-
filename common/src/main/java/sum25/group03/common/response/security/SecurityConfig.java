package sum25.group03.common.response.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {


    private final CustomJwtConverter customJwtConverter;

    public SecurityConfig(CustomJwtConverter customJwtConverter) {
        this.customJwtConverter = customJwtConverter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh-token",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/confirm-forgot-password",
                                "/api/v1/auth/privileges",
                                "/api/v1/auth/first-login-change-password"
                        ).permitAll()

                        .requestMatchers(
                                // PayPal return and cancel URLs
                                "/api/v1/paypal/return",
                                "/api/v1/paypal/cancel",

                                // VNPay IPN and return URLs
                                "/api/v1/payments/vnpay/ipn",
                                "/api/v1/payments/vnpay/return"
                        ).permitAll()
                        .requestMatchers("/actuator/prometheus").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwkSetUri(
                                                "https://cognito-idp.ap-southeast-2.amazonaws.com/ap-southeast-2_7UGXSOgJj/.well-known/jwks.json"

                                        )
                                        .jwtAuthenticationConverter(customJwtConverter)
                        )
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                );

        return http.build();
    }

}
