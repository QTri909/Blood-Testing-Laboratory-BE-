package sum25.group03.monitoringservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.service.RawTestResultService;

import java.util.List;

@RestController
@RequestMapping("/api/raw-results")
public class RawTestResultController {

    private final RawTestResultService rawTestResultService;

    public RawTestResultController(RawTestResultService rawTestResultService) {
        this.rawTestResultService = rawTestResultService;
    }

    // Add new raw test result
    @PostMapping
    public ResponseEntity<?> addRawTestResult(@RequestBody RawTestResult rawTestResult) {
        try {
            RawTestResult saved = rawTestResultService.addRawTestResult(rawTestResult);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error saving raw test result: " + e.getMessage());
        }
    }

    // Get raw test result by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRawTestResult(@PathVariable String id) {
        RawTestResult result = rawTestResultService.getRawTestResultById(id);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Verify raw test result
    @GetMapping("/verify/{id}")
    public ResponseEntity<?> verifyRawTestResult(@PathVariable String id) {
        boolean isMatch = rawTestResultService.verifyRawTestResult(id);
        return ResponseEntity.ok(isMatch ? "MATCH" : "MISMATCH");
    }

    // Get log of all backups
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getBackupLogs() {
        List<String> logs = rawTestResultService.getBackupLogs();
        return ResponseEntity.ok(logs);
    }

    // Retry failed insertions
    @PostMapping("/retry")
    public ResponseEntity<?> retryFailedInsertions() {
        int retriedCount = rawTestResultService.retryFailedInsertions();
        return ResponseEntity.ok("Retried failed insertions: " + retriedCount);
    }
}
