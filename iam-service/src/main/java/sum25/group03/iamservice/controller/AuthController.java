// AuthController.java
package sum25.group03.iamservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.iamservice.dto.request.*;
import sum25.group03.iamservice.dto.response.LoginResponse;
import sum25.group03.iamservice.dto.response.LoginWithRefresh;
import sum25.group03.iamservice.service.Interface.AuthService;

import jakarta.validation.Valid;
import sum25.group03.iamservice.service.Interface.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                            HttpServletResponse response) {

        LoginWithRefresh loginWithRefresh = authService.login(request);

        // Set refresh token vào cookie nếu có
        if (loginWithRefresh.refreshToken() != null) {
            Cookie refreshCookie = new Cookie("refreshToken", loginWithRefresh.refreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(30 * 24 * 60 * 60); // 30 ngày
            response.addCookie(refreshCookie);
        }

        return ApiResponse.data(loginWithRefresh.loginResponse())
                .message("Login successful")
                .build();
    }


    @PostMapping("/first-login-change-password")
    public ApiResponse<LoginResponse> firstLoginChangePassword(
            @RequestBody FirstLoginChangePasswordRequest req,
            HttpServletResponse response) {

        LoginWithRefresh loginWithRefresh = authService.firstLoginChangePassword(
                req.getUsername(),
                req.getSession(),
                req.getNewPassword()
        );

        // Set refresh token vào cookie nếu có
        if (loginWithRefresh.refreshToken() != null) {
            Cookie refreshCookie = new Cookie("refreshToken", loginWithRefresh.refreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(30 * 24 * 60 * 60);
            response.addCookie(refreshCookie);
        }

        return ApiResponse.data(loginWithRefresh.loginResponse())
                .message("First login password changed successfully")
                .build();
    }




    @GetMapping("/privileges")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Map<String, List<String>>> getUserPrivileges(@RequestParam String username) {
        Map<String, List<String>> data = userService.getRolesAndPrivilegesByUsername(username);
        return ApiResponse.data(data)
                .message("Privileges retrieved successfully")
                .build();
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> changePassword(@RequestBody PasswordChangeRequest req,
                                              HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        authService.changePassword(token, req.getOldPassword(), req.getNewPassword());
        return ApiResponse.data("Password changed successfully")
                .message("Password updated")
                .build();
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req.getUsername());
        return ApiResponse.data("Verification code sent to email")
                .message("Forgot password request processed")
                .build();
    }

    @PostMapping("/confirm-forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> confirmForgotPassword(@Valid @RequestBody ConfirmForgotPasswordRequest req) {
        authService.confirmForgotPassword(req.getUsername(), req.getConfirmationCode(), req.getNewPassword());
        return ApiResponse.data("Password has been reset successfully")
                .message("Password reset completed")
                .build();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> logout(@RequestHeader("Authorization") String authHeader,
                                      HttpServletResponse response) {

        String accessToken = authHeader.replace("Bearer ", "");
        authService.logout(accessToken);

        // Xóa refresh token cookie
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0); // Xóa cookie ngay lập tức
        response.addCookie(deleteCookie);

        return ApiResponse.data("User logged out successfully")
                .message("Logout completed")
                .build();
    }


    @PostMapping("/refresh-token")
    public ApiResponse<LoginResponse> refreshToken(HttpServletRequest request,
                                                   HttpServletResponse response) {

        // Lấy token mới từ service
        LoginWithRefresh loginWithRefresh = authService.refreshToken(request);

        // Set refresh token vào cookie (nếu có)
        if (loginWithRefresh.refreshToken() != null) {
            Cookie refreshCookie = new Cookie("refreshToken", loginWithRefresh.refreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(30 * 24 * 60 * 60); // 30 ngày
            response.addCookie(refreshCookie);

            // Set sub/cognitoUserId vào cookie để dùng lần sau
            Cookie subCookie = new Cookie("cognitoUserId", loginWithRefresh.loginResponse().getSub());
            subCookie.setHttpOnly(true);
            subCookie.setPath("/");
            subCookie.setMaxAge(30 * 24 * 60 * 60);
            response.addCookie(subCookie);
        }

        return ApiResponse.data(loginWithRefresh.loginResponse())
                .message("Token refreshed successfully")
                .build();
    }


}
