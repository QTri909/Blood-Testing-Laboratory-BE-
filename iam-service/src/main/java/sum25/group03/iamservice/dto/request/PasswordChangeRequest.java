package sum25.group03.iamservice.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}