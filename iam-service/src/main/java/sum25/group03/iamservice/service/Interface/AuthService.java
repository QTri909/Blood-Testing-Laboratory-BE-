package sum25.group03.iamservice.service.Interface;

import sum25.group03.iamservice.dto.request.LoginRequest;
import sum25.group03.iamservice.dto.request.RefreshTokenRequest;
import sum25.group03.iamservice.dto.response.LoginResponse;

public interface AuthService {


    LoginResponse login(LoginRequest request);
    LoginResponse firstLoginChangePassword(String username, String session, String newPassword);
    LoginResponse refreshToken(RefreshTokenRequest refreshToken);
    void changePassword(String accessToken, String oldPassword, String newPassword);

    void forgotPassword(String username);
    void confirmForgotPassword(String username, String confirmationCode, String newPassword);

    void logout(String accessToken);
}