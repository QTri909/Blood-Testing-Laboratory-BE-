package sum25.group03.iamservice.service;

import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.dto.response.UserResponse;


public interface UserService {
    UserResponse createUser(UserCreateRequest request);
}
