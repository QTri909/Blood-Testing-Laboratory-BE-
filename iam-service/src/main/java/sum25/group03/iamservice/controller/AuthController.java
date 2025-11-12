// AuthController.java
package sum25.group03.iamservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.iamservice.dto.request.*;
import sum25.group03.iamservice.dto.response.LoginResponse;
import sum25.group03.iamservice.service.Interface.AuthService;

import jakarta.validation.Valid;
import sum25.group03.iamservice.service.Interface.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.data(response)
                .message("Login successful")
                .build();
    }

    @PostMapping("/first-login-change-password")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<LoginResponse> firstLoginChangePassword(
            @RequestBody FirstLoginChangePasswordRequest req) {

        LoginResponse response = authService.firstLoginChangePassword(
                req.getUsername(),
                req.getSession(),
                req.getNewPassword()
        );

        return ApiResponse.data(response)
                .message("First login password changed successfully")
                .build();
    }







    @GetMapping("/privileges")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Map<String, List<String>>> getUserPrivileges(@RequestParam String email) {
        Map<String, List<String>> data = userService.getRolesAndPrivilegesByEmail(email);
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
        authService.forgotPassword(req.getEmail());
        return ApiResponse.data("Verification code sent to email")
                .message("Forgot password request processed")
                .build();
    }

    @PostMapping("/confirm-forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> confirmForgotPassword(@Valid @RequestBody ConfirmForgotPasswordRequest req) {
        authService.confirmForgotPassword(req.getEmail(), req.getConfirmationCode(), req.getNewPassword());
        return ApiResponse.data("Password has been reset successfully")
                .message("Password reset completed")
                .build();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> logout(@RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        authService.logout(accessToken);
        return ApiResponse.data("User logged out successfully")
                .message("Logout completed")
                .build();
    }

}
