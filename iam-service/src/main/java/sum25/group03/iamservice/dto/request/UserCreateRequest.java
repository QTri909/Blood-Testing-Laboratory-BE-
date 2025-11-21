package sum25.group03.iamservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;


    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Phone number must start with 0 and have 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @NotBlank(message = "Birth date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Birth date must be in yyyy-MM-dd format")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Identity number is required")
    @Pattern(regexp = "^[A-Z0-9]{6,12}$", message = "Identity number must be alphanumeric and 6–12 characters long")
    private String identityNumber;

    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    @NotBlank(message = "Role code is required")
    private String roleCode;
}


