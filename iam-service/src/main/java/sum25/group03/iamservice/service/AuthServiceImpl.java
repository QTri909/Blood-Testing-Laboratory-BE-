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
        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .authParameters(
                        Map.of(
                                "USERNAME", request.getEmail(),
                                "PASSWORD", request.getPassword(),
                                "SECRET_HASH", calculateSecretHash(request.getEmail())
                        )
                )
                .build();

        AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(authRequest);


        if (response.challengeName() == ChallengeNameType.NEW_PASSWORD_REQUIRED) {
            AdminRespondToAuthChallengeRequest challengeRequest = AdminRespondToAuthChallengeRequest.builder()
                    .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                    .clientId(clientId)
                    .userPoolId(userPoolId)
                    .challengeResponses(Map.of(
                            "USERNAME", request.getEmail(),
                            "NEW_PASSWORD", request.getPassword(),
                            "SECRET_HASH", calculateSecretHash(request.getEmail())
                    ))
                    .session(response.session())
                    .build();

            AdminRespondToAuthChallengeResponse challengeResponse =
                    cognitoClient.adminRespondToAuthChallenge(challengeRequest);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(challengeResponse.authenticationResult().accessToken());
            loginResponse.setRefreshToken(challengeResponse.authenticationResult().refreshToken());
            loginResponse.setIdToken(challengeResponse.authenticationResult().idToken());
            loginResponse.setExpiresIn(challengeResponse.authenticationResult().expiresIn());
            return loginResponse;
        }


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(response.authenticationResult().accessToken());
        loginResponse.setExpiresIn(response.authenticationResult().expiresIn());
        return loginResponse;
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
