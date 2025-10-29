package sum25.group03.iamservice.dto.request;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
    private String email;
}
