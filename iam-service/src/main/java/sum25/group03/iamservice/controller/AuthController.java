// AuthController.java
package sum25.group03.iamservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.iamservice.dto.request.ConfirmForgotPasswordRequest;
import sum25.group03.iamservice.dto.request.ForgotPasswordRequest;
import sum25.group03.iamservice.dto.request.LoginRequest;
import sum25.group03.iamservice.dto.request.PasswordChangeRequest;
import sum25.group03.iamservice.dto.response.LoginResponse;
import sum25.group03.iamservice.service.AuthService;

import jakarta.validation.Valid;
import sum25.group03.iamservice.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/privileges")
    public ResponseEntity<?> getUserPrivileges(@RequestParam String email) {
        Map<String, List<String>> data = userService.getRolesAndPrivilegesByEmail(email);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody PasswordChangeRequest req,
                                 HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        authService.changePassword(token, req.getOldPassword(), req.getNewPassword());
        return "Password changed successfully";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req.getEmail());
        return ResponseEntity.ok("Verification code sent to email");
    }

    @PostMapping("/confirm-forgot-password")
    public ResponseEntity<String> confirmForgotPassword(@Valid @RequestBody ConfirmForgotPasswordRequest req) {
        authService.confirmForgotPassword(req.getEmail(), req.getConfirmationCode(), req.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        authService.logout(accessToken);
        return ResponseEntity.ok("User logged out successfully");
    }


}
