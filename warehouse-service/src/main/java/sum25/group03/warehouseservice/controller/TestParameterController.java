package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.request.TestTemplateReq;
import sum25.group03.warehouseservice.entity.enums.TestType;
import sum25.group03.warehouseservice.service.testparameter.TestParameterService;

@RestController
@RequestMapping("/api/v1/test-parameters")
@RequiredArgsConstructor
public class TestParameterController {
    private final TestParameterService testParameterService;
    @GetMapping("")
    public ApiResponse<?> testParameter(@RequestParam TestType testType) {
        return ApiResponse.ok(testParameterService.getGlobalTestParameters(testType));
    }

    @PostMapping("")
    public ApiResponse<?> createTestTemplate(@RequestBody TestTemplateReq request) {
        return ApiResponse.ok(testParameterService.addTestTemplate(request));
    }
}
