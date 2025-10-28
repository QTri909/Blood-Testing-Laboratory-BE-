package sum25.group03.iamservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.dto.request.UserUpdateRequest;
import sum25.group03.iamservice.dto.response.UserResponse;

import java.util.List;


public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deactivateUser(Long id);
    void deleteUser(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> getAllPatients(Pageable pageable);
    UserResponse getUserById(Long id);

}
