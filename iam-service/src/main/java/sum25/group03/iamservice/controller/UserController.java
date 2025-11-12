package sum25.group03.iamservice.controller;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.dto.request.UserUpdateRequest;
import sum25.group03.iamservice.dto.response.UserResponse;
import sum25.group03.iamservice.service.Interface.UserService;





@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateUser(id, request);
        return ApiResponse.data(response)
                .message("User updated successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ApiResponse.data(response)
                .message("User created successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('USER_LOCK')")
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ApiResponse.data("User deactivated successfully")
                .message("Deactivation completed")
                .build();
    }

    @PreAuthorize("hasAuthority('USER_DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping("/patients")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<UserResponse>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> result = userService.getAllPatients(pageable);

        return ApiResponse.data(result)
                .message("Patients retrieved successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> result = userService.getAllUsers(pageable);

        return ApiResponse.data(result)
                .message("Users retrieved successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ApiResponse.data(response)
                .message("User retrieved successfully")
                .build();
    }
}