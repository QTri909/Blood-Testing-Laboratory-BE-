package sum25.group03.patientservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.patientservice.dtos.request.UserSnapshotRequest;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.services.interfaces.UserSnapshotService;

import java.util.List;

@RestController
@RequestMapping("/api/user-snapshots")
@RequiredArgsConstructor
public class UserSnapshotController {

    private final UserSnapshotService service;

    @PostMapping
    public ResponseEntity<UserSnapshotResponse> create(@RequestBody UserSnapshotRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSnapshotResponse> update(@PathVariable Long id,
                                                       @RequestBody UserSnapshotRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserSnapshotResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSnapshotResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/external/{externalUserId}")
    public ResponseEntity<UserSnapshotResponse> getByExternalUserId(@PathVariable Long externalUserId) {
        return ResponseEntity.ok(service.getByExternalUserId(externalUserId));
    }
}
