package sum25.group03.iamservice.service;

import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.entity.User;

public interface CognitoService {


    String createUser(UserCreateRequest request);


    String createAdminUser(String email, String temporaryPassword);

    void updateUserAttributes(User user);
    void disableUser(String email);
    void enableUser(String email);
    void deleteUser(String email);







}
