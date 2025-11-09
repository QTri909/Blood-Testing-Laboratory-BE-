package sum25.group03.iamservice.service.Interface;

import sum25.group03.iamservice.dto.request.LoginRequest;
import sum25.group03.iamservice.dto.request.RefreshTokenRequest;
import sum25.group03.iamservice.dto.response.LoginResponse;

public interface AuthService {


    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest refreshToken);
    void changePassword(String accessToken, String oldPassword, String newPassword);

    void forgotPassword(String email);
    void confirmForgotPassword(String email, String confirmationCode, String newPassword);

    void logout(String accessToken);
}