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
import sum25.group03.testorderservice.services.interfaces.TestResultService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-result") //  {{api_gateway}}/api/v1/test-orders/test-result
@Slf4j
@RequiredArgsConstructor
public class TestResultController {

    @Autowired
    private TestResultService testResultService;

    @PostMapping("/review-test-result")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> reviewTestResult(
            @RequestParam("testResultId") @NotNull Long testResultId,
            @RequestParam("adjustedValue") Double adjustedValue,
            @RequestParam("reviewId") @NotNull Long reviewId
    ) {
        testResultService.reviewTestResult(testResultId, adjustedValue, reviewId);
        return ApiResponse.add("Review Test Result successfully", null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TestResultResponseDTO> createTestResult(@Valid @RequestBody TestResultRequestDTO requestDTO) {
        TestResultResponseDTO response = testResultService.createTestResult(requestDTO);
        return ApiResponse.add("Create Test Result successfully", response);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TestResultResponseDTO> updateTestResult(
            @PathVariable Long id,
            @Valid @RequestBody TestResultRequestDTO requestDTO)
    {
        TestResultResponseDTO response = testResultService.updateTestResult(id, requestDTO);
        return ApiResponse.add("Update Test Result successfully", response);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteTestResult(@PathVariable Long id) {
        testResultService.deleteTestResult(id);
        return ApiResponse.add("Delete Test Result successfully", null);
    }

    @GetMapping ("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TestResultResponseDTO> getTestResultById(@PathVariable Long id) {
        TestResultResponseDTO response = testResultService.getTestResultById(id);
        return ApiResponse.add("Get Test Result successfully", response);
    }

    @GetMapping("/test-order/{testOrderId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<TestResultResponseDTO>> getTestResultsByTestOrderId(@PathVariable Long testOrderId) {
        java.util.List<TestResultResponseDTO> responses = testResultService.getTestResultsByTestOrderId(testOrderId);
        return ApiResponse.add("Get Test Results successfully", responses);
    }


}
