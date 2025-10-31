package sum25.group03.iamservice.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ConfirmForgotPasswordRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String confirmationCode;

    @NotBlank
    private String newPassword;
}