package sum25.group03.iamservice.controller;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.dto.request.UserUpdateRequest;
import sum25.group03.iamservice.dto.response.UserResponse;
import sum25.group03.iamservice.service.UserService;





@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/patients")
    public ResponseEntity<Page<UserResponse>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllPatients(pageable));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }






}
