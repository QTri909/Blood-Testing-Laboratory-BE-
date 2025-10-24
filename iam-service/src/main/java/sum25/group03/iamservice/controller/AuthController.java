// AuthController.java
package sum25.group03.iamservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.group03.iamservice.dto.request.LoginRequest;
import sum25.group03.iamservice.dto.response.LoginResponse;
import sum25.group03.iamservice.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }


}
