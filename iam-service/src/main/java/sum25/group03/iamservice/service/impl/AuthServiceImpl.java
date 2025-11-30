package sum25.group03.iamservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.services.interfaces.CommonRedisService;
import sum25.group03.iamservice.dto.CognitoConfig;
import sum25.group03.iamservice.dto.request.LoginRequest;
import sum25.group03.iamservice.dto.response.LoginResponse;
import sum25.group03.iamservice.dto.response.LoginWithRefresh;
import sum25.group03.iamservice.entity.User;
import sum25.group03.iamservice.event.PasswordChangedEvent;
import sum25.group03.iamservice.repository.UserRepository;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import sum25.group03.iamservice.service.Interface.AuthService;
import sum25.group03.iamservice.service.KafkaProducerService;



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
    private final SecretService secretService;
    private final KafkaProducerService kafkaProducerService;
    private final CommonRedisService commonRedisService;

    private final String secretName = "IAMService/CognitoConfig";


    private CognitoConfig getConfig() {
        return secretService.getCognitoConfig(secretName);
    }


    private String calculateSecretHash(String username, String clientSecret, String clientId) {
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
    public LoginWithRefresh login(LoginRequest request) {
        CognitoConfig config = getConfig();

        User user = userRepository.findByEmailOrIdentityNumber(
                request.getUsername(), request.getUsername()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        String cognitoUsername = user.getEmail();
        String userEmail = user.getEmail();

        // Kiểm tra account khóa
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
            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .userPoolId(config.getUserPoolId())
                    .clientId(config.getClientId())
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .authParameters(Map.of(
                            "USERNAME", cognitoUsername,
                            "PASSWORD", request.getPassword(),
                            "SECRET_HASH", calculateSecretHash(cognitoUsername, config.getClientSecret(), config.getClientId())
                    ))
                    .build();

            AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(authRequest);

            if (response.challengeName() != null &&
                    response.challengeName() == ChallengeNameType.NEW_PASSWORD_REQUIRED) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setFirstLogin(true);
                loginResponse.setSession(response.session());
                loginResponse.setChallenge(response.challengeNameAsString());
                return new LoginWithRefresh(loginResponse, null);
            }

            if (response.authenticationResult() == null) {
                throw new RuntimeException("Authentication failed: no authenticationResult returned");
            }

            resetFailedAttempts(user);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(response.authenticationResult().accessToken());
            loginResponse.setIdToken(response.authenticationResult().idToken());
            loginResponse.setExpiresIn(response.authenticationResult().expiresIn());
            loginResponse.setSub(user.getCognitoUserId());

            String refreshToken = response.authenticationResult().refreshToken();

            // store accessToken in redis:
            commonRedisService.saveValue(
                    user.getCognitoUserId(),
                    loginResponse.getAccessToken(),
                    loginResponse.getExpiresIn()
            );
            System.out.println("Saved accessToken in Redis for user: " + user.getCognitoUserId());

            return new LoginWithRefresh(loginResponse, refreshToken);

        } catch (NotAuthorizedException | UserNotFoundException e) {
            handleFailedAttempt(user);
            throw new RuntimeException("Invalid credentials. Failed attempts: " + user.getFailedAttempts());
        } catch (UserNotConfirmedException e) {
            throw new RuntimeException("EMAIL_NOT_CONFIRMED");
        } catch (PasswordResetRequiredException e) {
            throw new RuntimeException("RESET_REQUIRED");
        } catch (InvalidParameterException e) {
            throw new RuntimeException("INVALID_PARAMETER: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Authentication error: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginWithRefresh firstLoginChangePassword(String username, String session, String newPassword) {
        CognitoConfig config = getConfig();
        User user = userRepository.findByEmailOrIdentityNumber(username, username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String cognitoUsername = user.getEmail();

        try {
            RespondToAuthChallengeRequest challengeRequest = RespondToAuthChallengeRequest.builder()
                    .clientId(config.getClientId())
                    .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                    .session(session)
                    .challengeResponses(Map.of(
                            "USERNAME", cognitoUsername,
                            "NEW_PASSWORD", newPassword,
                            "SECRET_HASH", calculateSecretHash(cognitoUsername, config.getClientSecret(), config.getClientId())
                    ))
                    .build();

            RespondToAuthChallengeResponse response = cognitoClient.respondToAuthChallenge(challengeRequest);

            if (response.authenticationResult() == null) {
                throw new RuntimeException("First login password change failed: no authenticationResult returned");
            }

            cognitoClient.adminUpdateUserAttributes(AdminUpdateUserAttributesRequest.builder()
                    .userPoolId(config.getUserPoolId())
                    .username(cognitoUsername)
                    .userAttributes(
                            AttributeType.builder()
                                    .name("email_verified")
                                    .value("true")
                                    .build()
                    )
                    .build()
            );

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(response.authenticationResult().accessToken());
            loginResponse.setIdToken(response.authenticationResult().idToken());
            loginResponse.setExpiresIn(response.authenticationResult().expiresIn());
            loginResponse.setSub(user.getCognitoUserId());

            String refreshToken = response.authenticationResult().refreshToken();

            return new LoginWithRefresh(loginResponse, refreshToken);

        } catch (Exception e) {
            throw new RuntimeException("First login password change error: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginWithRefresh refreshToken(HttpServletRequest request) {
        CognitoConfig config = getConfig();

        // 1. Lấy refresh token và cognitoUserId từ cookie
        String refreshToken = null;
        String cognitoUserId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("refreshToken".equals(c.getName())) refreshToken = c.getValue();
                if ("cognitoUserId".equals(c.getName())) cognitoUserId = c.getValue();
            }
        }
        if (refreshToken == null || cognitoUserId == null) {
            throw new RuntimeException("Missing refresh token or cognitoUserId in cookies");
        }

        // 2. Lấy user từ DB bằng cognitoUserId
        User user = userRepository.findByCognitoUserId(cognitoUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Gọi Cognito REFRESH_TOKEN_AUTH
        AdminInitiateAuthRequest refreshRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(config.getUserPoolId())
                .clientId(config.getClientId())
                .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                .authParameters(Map.of(
                        "REFRESH_TOKEN", refreshToken,
                        "USERNAME", user.getCognitoUserId(),
                        "SECRET_HASH", calculateSecretHash(user.getCognitoUserId(), config.getClientSecret(), config.getClientId())
                ))
                .build();

        AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(refreshRequest);

        if (response.authenticationResult() == null) {
            throw new RuntimeException("Refresh token invalid or expired");
        }

        // 4. Trả về access + id token mới, refresh token vẫn giữ nguyên
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(response.authenticationResult().accessToken());
        loginResponse.setIdToken(response.authenticationResult().idToken());
        loginResponse.setExpiresIn(response.authenticationResult().expiresIn());
        loginResponse.setSub(user.getCognitoUserId());

        return new LoginWithRefresh(loginResponse, refreshToken);
    }







    @Override
    public void changePassword(String accessToken, String oldPassword, String newPassword) {
        try {
            cognitoClient.changePassword(builder -> builder
                    .accessToken(accessToken)
                    .previousPassword(oldPassword)
                    .proposedPassword(newPassword)
            );
            try {
                User user = userRepository.findAll().stream()
                        .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase("unknown")) // không resolve từ token được
                        .findFirst()
                        .orElse(null);

                PasswordChangedEvent event = new PasswordChangedEvent(
                        user != null ? user.getId() : null,
                        user != null ? user.getEmail() : "unknown",
                        LocalDateTime.now().toString()
                );
                kafkaProducerService.sendPasswordChanged(event);
            } catch (Exception ignored) {
            }
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Change password failed: " + e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public void forgotPassword(String username) {
        CognitoConfig config = getConfig();

        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .clientId(config.getClientId())
                .username(username)
                .secretHash(calculateSecretHash(username, config.getClientSecret(), config.getClientId()))
                .build();

        cognitoClient.forgotPassword(request);
    }

    @Override
    public void confirmForgotPassword(String username, String confirmationCode, String newPassword) {
        CognitoConfig config = getConfig();

        ConfirmForgotPasswordRequest confirmRequest = ConfirmForgotPasswordRequest.builder()
                .clientId(config.getClientId())
                .username(username)
                .confirmationCode(confirmationCode)
                .password(newPassword)
                .secretHash(calculateSecretHash(username, config.getClientSecret(), config.getClientId()))
                .build();

        cognitoClient.confirmForgotPassword(confirmRequest);
    }

    @Override
    public void logout(String accessToken) {
        try {
            GlobalSignOutRequest signOutRequest = GlobalSignOutRequest.builder()
                    .accessToken(accessToken)
                    .build();

            cognitoClient.globalSignOut(signOutRequest);
        } catch (NotAuthorizedException e) {
            throw new RuntimeException("Invalid or expired access token");
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage(), e);
        }
    }

    // ---- Các helper cho failed attempts ----
    private void handleFailedAttempt(User user) {
        int newAttempts = (user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1;
        user.setFailedAttempts(newAttempts);
        user.setLastFailedAt(LocalDateTime.now());

        if (newAttempts >= 5) {
            user.setAccountNonLocked(false);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
        }

        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLastFailedAt(null);
        user.setAccountNonLocked(true);
        user.setLockedUntil(null);
        userRepository.save(user);
    }
}
