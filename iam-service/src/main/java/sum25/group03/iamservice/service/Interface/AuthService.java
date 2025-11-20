package sum25.group03.iamservice.service.Interface;

import jakarta.servlet.http.HttpServletRequest;
import sum25.group03.iamservice.dto.request.LoginRequest;
import sum25.group03.iamservice.dto.response.LoginWithRefresh;

public interface AuthService {

    LoginWithRefresh login(LoginRequest request);
    LoginWithRefresh firstLoginChangePassword(String username, String session, String newPassword);
    LoginWithRefresh refreshToken(HttpServletRequest request);
    void changePassword(String accessToken, String oldPassword, String newPassword);

    void forgotPassword(String username);
    void confirmForgotPassword(String username, String confirmationCode, String newPassword);

    void logout(String accessToken);
}