package sum25.group03.iamservice.service;

import sum25.group03.iamservice.dto.request.UserCreateRequest;

public interface CognitoService {


    String createUser(UserCreateRequest request);

    String createAdminUser(String email, String temporaryPassword);




}
