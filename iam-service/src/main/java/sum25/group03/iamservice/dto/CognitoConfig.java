package sum25.group03.iamservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CognitoConfig {

    private String userPoolId;
    private String clientId;
    private String clientSecret;
}
