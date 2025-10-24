package sum25.group03.iamservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.iamservice.dto.request.LoginRequest;
import sum25.group03.iamservice.dto.request.RefreshTokenRequest;
import sum25.group03.iamservice.dto.response.LoginResponse;
import sum25.group03.iamservice.entity.User;
import sum25.group03.iamservice.repository.UserRepository;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CognitoIdentityProviderClient cognitoClient;
    private final UserRepository userRepository;

    private final String userPoolId = "ap-southeast-2_7UGXSOgJj";
    private final String clientId = "3d4f468iu5ddtc8boqv7v2t0d";
    private final String clientSecret = "12p7igdbailq0evc8ba9ka0a3k2rkcj8rj42tfs5esbf159hmle8";


    private String calculateSecretHash(String username) {
        try {
            String message = username + clientId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clientSecret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating SECRET_HASH", e);
        }
    }



    @Override
    public LoginResponse login(LoginRequest request) {
        // 🔹 1. Lấy user từ DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔹 2. Kiểm tra account khóa
        if (!user.getAccountNonLocked()) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Account is locked until " + user.getLockedUntil());
            } else {
                user.setAccountNonLocked(true);
                user.setFailedAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }
        }

        try {
            // 🔹 3. Dùng InitiateAuth thay vì AdminInitiateAuth
            software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest authRequest =
                    software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest.builder()
                            .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                            .clientId(clientId)
                            .authParameters(Map.of(
                                    "USERNAME", request.getEmail(),
                                    "PASSWORD", request.getPassword(),
                                    "SECRET_HASH", calculateSecretHash(request.getEmail())
                            ))
                            .build();

            software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse response =
                    cognitoClient.initiateAuth(authRequest);

            // 🔹 4. Nếu login thành công
            user.setFailedAttempts(0);
            user.setAccountNonLocked(true);
            user.setLockedUntil(null);
            userRepository.save(user);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(response.authenticationResult().accessToken());
            loginResponse.setRefreshToken(response.authenticationResult().refreshToken());
            loginResponse.setIdToken(response.authenticationResult().idToken());
            loginResponse.setExpiresIn(response.authenticationResult().expiresIn());

            return loginResponse;

        } catch (Exception e) {
            // 🔹 5. Nếu login thất bại → tăng số lần thất bại
            int newAttempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(newAttempts);
            user.setLastFailedAt(LocalDateTime.now());

            if (newAttempts >= 5) {
                user.setAccountNonLocked(false);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
            }

            userRepository.save(user);
            throw new RuntimeException("Invalid credentials. Failed attempts: " + newAttempts);
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        try {
            AdminInitiateAuthRequest refreshRequest = AdminInitiateAuthRequest.builder()
                    .userPoolId(userPoolId)
                    .clientId(clientId)
                    .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .authParameters(Map.of(
                            "REFRESH_TOKEN", request.getRefreshToken(),
                            "SECRET_HASH", calculateSecretHash(request.getEmail())
                    ))
                    .build();

            AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(refreshRequest);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(response.authenticationResult().accessToken());
            loginResponse.setIdToken(response.authenticationResult().idToken());
            loginResponse.setExpiresIn(response.authenticationResult().expiresIn());

            loginResponse.setRefreshToken(request.getRefreshToken());

            return loginResponse;
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired refresh token: " + e.getMessage(), e);
        }
    }


}
