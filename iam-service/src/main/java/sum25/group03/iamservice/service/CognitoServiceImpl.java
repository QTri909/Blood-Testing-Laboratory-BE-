package sum25.group03.iamservice.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import sum25.group03.iamservice.dto.request.UserCreateRequest;


import java.util.List;
import java.util.Map;

@Service
public class CognitoServiceImpl implements CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId = "ap-southeast-2_7UGXSOgJj";
    private final String clientId = "3d4f468iu5ddtc8boqv7v2t0d";

    public CognitoServiceImpl(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }


    @Override
    public String createUser(UserCreateRequest request) {
        AdminCreateUserRequest createUserRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(request.getEmail())
                .temporaryPassword("TempPass123!") // ✅ password tạm
                .userAttributes(
                        AttributeType.builder().name("email").value(request.getEmail()).build(),
                        AttributeType.builder().name("name").value(request.getFullName()).build(),
                        AttributeType.builder().name("phone_number").value(request.getPhoneNumber()).build(),
                        AttributeType.builder().name("email_verified").value("true").build() // ✅ xác thực email
                )
                .messageAction(MessageActionType.SUPPRESS)
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL) // tùy chọn
                .forceAliasCreation(false)
                .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(createUserRequest);

        // ✅ Đảm bảo user ở trạng thái "FORCE_CHANGE_PASSWORD"
        cognitoClient.adminUpdateUserAttributes(AdminUpdateUserAttributesRequest.builder()
                .userPoolId(userPoolId)
                .username(request.getEmail())
                .userAttributes(AttributeType.builder().name("email_verified").value("true").build())
                .build());

        return response.user().username();
    }

    @Override
    public String createAdminUser(String email, String temporaryPassword) {
        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .temporaryPassword(temporaryPassword)
                .userAttributes(List.of(
                        AttributeType.builder().name("email").value(email).build(),
                        AttributeType.builder().name("email_verified").value("true").build()
                ))
//                .messageAction(MessageActionType.SUPPRESS) // Không gửi email tự động
                .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(request);

        return response.user().username(); // Đây là Cognito User ID
    }

    @Override
    public void disableUser(String cognitoUserId) {
        try {
            AdminDisableUserRequest disableRequest = AdminDisableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(cognitoUserId)
                    .build();

            cognitoClient.adminDisableUser(disableRequest);
            System.out.println("User disabled successfully in Cognito: " + cognitoUserId);
        } catch (CognitoIdentityProviderException e) {
            System.err.println("Failed to disable user in Cognito: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    @Override
    public void deleteUser(String cognitoUserId) {
        try {
            AdminDeleteUserRequest deleteRequest = AdminDeleteUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(cognitoUserId)
                    .build();

            cognitoClient.adminDeleteUser(deleteRequest);
            System.out.println("✅ User deleted successfully in Cognito: " + cognitoUserId);
        } catch (CognitoIdentityProviderException e) {
            System.err.println("❌ Failed to delete user in Cognito: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

}
