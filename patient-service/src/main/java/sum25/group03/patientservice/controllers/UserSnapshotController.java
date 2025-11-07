package sum25.group03.patientservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.patientservice.dtos.request.UserSnapshotRequest;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.services.interfaces.UserSnapshotService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-snapshots")
@RequiredArgsConstructor
public class UserSnapshotController {

    private final UserSnapshotService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserSnapshotResponse> create(@RequestBody UserSnapshotRequest request) {
        return ApiResponse.add("Created", service.create(request));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserSnapshotResponse> update(@PathVariable Long id,
                                                       @RequestBody UserSnapshotRequest request) {
        return ApiResponse.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<UserSnapshotResponse>> getAll() {
        return ApiResponse.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserSnapshotResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @GetMapping("/external/{externalUserId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserSnapshotResponse> getByExternalUserId(@PathVariable Long externalUserId) {
        return ApiResponse.ok(service.getByExternalUserId(externalUserId));
    }

    // Cuong
    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.CREATED)
    public void syncUserSnapshots() {
        service.syncUserSnapshots();
    }
}
