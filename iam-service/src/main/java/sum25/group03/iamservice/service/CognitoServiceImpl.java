package sum25.group03.iamservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import sum25.group03.iamservice.dto.CognitoConfig;
import sum25.group03.iamservice.dto.request.UserCreateRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CognitoServiceImpl implements CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final SecretService secretService;
    private final String secretName = "IAMService/CognitoConfig";

    /**
     * Lấy CognitoConfig từ SecretService mỗi lần cần
     */
    private CognitoConfig getConfig() {
        return secretService.getCognitoConfig(secretName);
    }

    @Override
    public String createUser(UserCreateRequest request) {
        CognitoConfig config = getConfig();

        AdminCreateUserRequest createUserRequest = AdminCreateUserRequest.builder()
                .userPoolId(config.getUserPoolId())
                .username(request.getEmail())
                .temporaryPassword("TempPass123!") // password tạm
                .userAttributes(
                        AttributeType.builder().name("email").value(request.getEmail()).build(),
                        AttributeType.builder().name("name").value(request.getFullName()).build(),
                        AttributeType.builder().name("phone_number").value(request.getPhoneNumber()).build()

                )
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .forceAliasCreation(false)
                .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(createUserRequest);


        return response.user().username();
    }

    @Override
    public String createAdminUser(String email, String temporaryPassword) {
        CognitoConfig config = getConfig();

        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .userPoolId(config.getUserPoolId())
                .username(email)
                .temporaryPassword(temporaryPassword)
                .userAttributes(List.of(
                        AttributeType.builder().name("email").value(email).build()
                ))
                .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(request);

        return response.user().username();
    }
}
