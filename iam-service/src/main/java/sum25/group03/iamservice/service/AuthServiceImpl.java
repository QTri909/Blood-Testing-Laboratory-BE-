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
                // nếu quá hạn khóa thì mở khóa
                user.setAccountNonLocked(true);
                user.setFailedAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }
        }

        try {
            // 🔹 3. Dùng InitiateAuth thay vì AdminInitiateAuth
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(Map.of(
                            "USERNAME", request.getEmail(),
                            "PASSWORD", request.getPassword(),
                            "SECRET_HASH", calculateSecretHash(request.getEmail())
                    ))
                    .build();

            InitiateAuthResponse response = cognitoClient.initiateAuth(authRequest);

            // 🔹 3.a Nếu Cognito trả challenge (ví dụ NEW_PASSWORD_REQUIRED) -> không tính là failed attempt
            if (response.challengeName() != null) {
                String challenge = response.challengeNameAsString();
                // Nếu FE cần session để RespondToAuthChallenge, có thể trả session trong exception message
                String session = response.session();
                // Trả lỗi / thông báo đặc biệt cho FE xử lý (FE sẽ gọi API đổi mật khẩu hoặc RespondToAuthChallenge)
                throw new RuntimeException("FIRST_LOGIN: " + challenge + (session != null ? ("; session=" + session) : ""));
            }

            // 🔹 4. Nếu login thành công (authenticationResult != null)
            if (response.authenticationResult() == null) {
                // Unexpected - không có authenticationResult và không có challenge
                throw new RuntimeException("Authentication failed: no authenticationResult returned");
            }

            // Reset failed attempts khi login thành công
            resetFailedAttempts(user);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(response.authenticationResult().accessToken());
            loginResponse.setRefreshToken(response.authenticationResult().refreshToken());
            loginResponse.setIdToken(response.authenticationResult().idToken());
            loginResponse.setExpiresIn(response.authenticationResult().expiresIn());

            return loginResponse;

        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException |
                 software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException e) {
            // 🔹 Sai credentials (email hoặc password) -> tăng số lần thất bại
            handleFailedAttempt(user);
            throw new RuntimeException("Invalid credentials. Failed attempts: " + user.getFailedAttempts());

        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException e) {
            // 🔹 Tài khoản chưa xác nhận email/phone
            throw new RuntimeException("EMAIL_NOT_CONFIRMED");

        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.PasswordResetRequiredException e) {
            // 🔹 Cognito yêu cầu reset mật khẩu
            throw new RuntimeException("RESET_REQUIRED");

        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException e) {
            // 🔹 Parameter không hợp lệ (vd: email format)
            throw new RuntimeException("INVALID_PARAMETER: " + e.getMessage(), e);

        } catch (Exception e) {
            // 🔹 Các lỗi khác (network, AWS sdk, v.v.)
            throw new RuntimeException("Authentication error: " + e.getMessage(), e);
        }
    }

    /**
     * Tăng failedAttempts, set last failed time, và khóa account nếu vượt quá ngưỡng.
     * Lưu user về DB.
     */
    private void handleFailedAttempt(User user) {
        int newAttempts = (user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1;
        user.setFailedAttempts(newAttempts);
        user.setLastFailedAt(LocalDateTime.now());

        if (newAttempts >= 5) {
            user.setAccountNonLocked(false);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15)); // khóa 15 phút, tuỳ chỉnh
        }

        userRepository.save(user);
    }

    /**
     * Reset failedAttempts và lưu.
     */
    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLastFailedAt(null);
        user.setAccountNonLocked(true);
        user.setLockedUntil(null);
        userRepository.save(user);
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

    @Override
    public void changePassword(String accessToken, String oldPassword, String newPassword) {
        try {
            cognitoClient.changePassword(builder -> builder
                    .accessToken(accessToken)
                    .previousPassword(oldPassword)
                    .proposedPassword(newPassword)
            );
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Change password failed: " + e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public void forgotPassword(String email) {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .clientId(clientId)
                .username(email)
                .secretHash(calculateSecretHash(email))
                .build();

        cognitoClient.forgotPassword(request);
    }

    @Override
    public void confirmForgotPassword(String email, String confirmationCode, String newPassword) {
        ConfirmForgotPasswordRequest confirmRequest = ConfirmForgotPasswordRequest.builder()
                .clientId(clientId)
                .username(email)
                .confirmationCode(confirmationCode)
                .password(newPassword)
                .secretHash(calculateSecretHash(email))
                .build();

        cognitoClient.confirmForgotPassword(confirmRequest);
    }


}
