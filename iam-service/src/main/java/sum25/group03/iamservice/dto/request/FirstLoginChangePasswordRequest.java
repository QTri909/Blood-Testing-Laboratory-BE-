package sum25.group03.iamservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FirstLoginChangePasswordRequest {


    @NotBlank
    private String session;

    @NotBlank
    private String username;

    @NotBlank
    private String newPassword;
}
