package sum25.group03.iamservice.service.Interface;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.iamservice.dto.request.UserFilterSearchingRequest;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.dto.request.UserUpdateRequest;
import sum25.group03.iamservice.dto.response.UserResponse;

import java.util.List;
import java.util.Map;


public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deactivateUser(Long id);
    void deleteUser(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> getAllPatients(Pageable pageable);

    Map<String, List<String>> getRolesAndPrivilegesByEmail(String email);

    UserResponse getUserById(Long id);
    UserResponse getUserByIdentityNumber(String identityNumber);
    String approvePendingUser(Long id);

    Page<UserResponse> searchFilteredUsers(UserFilterSearchingRequest request);
}
