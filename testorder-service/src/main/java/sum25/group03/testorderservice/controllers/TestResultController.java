package sum25.group03.testorderservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.dtos.request.TestResultRequestDTO;
import sum25.group03.testorderservice.dtos.response.TestResultResponseDTO;
import sum25.group03.testorderservice.services.impl.CohereServiceImpl;
import sum25.group03.testorderservice.services.interfaces.TestResultService;

@RestController
@RequestMapping("/api/test-result")
@Slf4j
@RequiredArgsConstructor
public class TestResultController {

    @Autowired
    private TestResultService testResultService;

    @Autowired
    private CohereServiceImpl cohereService;

    @PostMapping("/review-test-result")
    public ResponseEntity<?> reviewTestResult(
            @RequestParam("testResultId") @NotNull Long testResultId,
            @RequestParam("adjustedValue") Double adjustedValue,
            @RequestParam("reviewId") @NotNull Long reviewId
    ) {
        testResultService.reviewTestResult(testResultId, adjustedValue, reviewId);
        return ResponseEntity.ok("✅ Test result reviewed successfully by user " + reviewId);
    }

    @PostMapping
    public ResponseEntity<TestResultResponseDTO> createTestResult(@Valid @RequestBody TestResultRequestDTO requestDTO) {
        log.info("API - Create Test Result");
        TestResultResponseDTO response = testResultService.createTestResult(requestDTO);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TestResultResponseDTO> updateTestResult(
            @PathVariable Long id,
            @Valid @RequestBody TestResultRequestDTO requestDTO) {
        log.info("API - Update Test Result id: {}", id);
        TestResultResponseDTO response = testResultService.updateTestResult(id, requestDTO);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestResult(@PathVariable Long id) {
        log.info("API - Delete Test Result id: {}", id);
        testResultService.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<TestResultResponseDTO> getTestResultById(@PathVariable Long id) {
        log.info("API - Get Test Result by id: {}", id);
        TestResultResponseDTO response = testResultService.getTestResultById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-order/{testOrderId}")
    public ResponseEntity<java.util.List<TestResultResponseDTO>> getTestResultsByTestOrderId(@PathVariable Long testOrderId) {
        log.info("API - Get Test Results by Test Order id: {}", testOrderId);
        java.util.List<TestResultResponseDTO> responses = testResultService.getTestResultsByTestOrderId(testOrderId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/ai-review")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> aiReview(@PathVariable Long id) {
        String reviewed = cohereService.jugeReview(id);
        return ApiResponse.ok("AI reviewed successfully",reviewed);
    }
}
