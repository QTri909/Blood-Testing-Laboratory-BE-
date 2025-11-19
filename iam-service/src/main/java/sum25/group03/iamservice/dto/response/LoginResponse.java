package sum25.group03.iamservice.dto.response;


import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private long expiresIn;
    private String refreshToken;
    private String idToken;

    private boolean firstLogin = false;
    private String session;
    private String challenge;

}
