package sum25.group03.warehouseservice.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.request.TestParameterReq;
import sum25.group03.warehouseservice.dto.request.TestTemplateReq;
import sum25.group03.warehouseservice.dto.request.UnusedTestParameterReq;
import sum25.group03.warehouseservice.dto.response.ParameterRes;
import sum25.group03.warehouseservice.dto.response.UnusedTestParameterRes;
import sum25.group03.warehouseservice.entity.enums.TestType;
import sum25.group03.warehouseservice.service.testparameter.TestParameterService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-parameters")
@RequiredArgsConstructor
public class TestParameterController {
    private final TestParameterService testParameterService;

    @PreAuthorize("hasAuthority('LAB_VIEW') ")
    @GetMapping("")
    public ApiResponse<?> testParameter(@RequestParam TestType testType) {
        return ApiResponse.ok(testParameterService.getGlobalTestParameters(testType));
    }
    @PreAuthorize("hasAuthority('LAB_UPDATE') ")
    @PostMapping("")
    public ApiResponse<?> createTestTemplate(@RequestBody TestTemplateReq request) {
        return ApiResponse.ok(testParameterService.addTestTemplate(request));
    }
    @PreAuthorize("hasAuthority('LAB_UPDATE') ")
    @PostMapping("/parameter")
    public ApiResponse<?> createTestParameter(@RequestBody TestParameterReq request) {
        return ApiResponse.ok(testParameterService.addTestParameter(request));
    }

    @PreAuthorize("hasAuthority('LAB_VIEW')")
    @GetMapping("/all")
    public ApiResponse<?> getAllTestParameter() {
        return ApiResponse.ok(testParameterService.getAllTestParameter());
    }

    @PreAuthorize("hasAuthority('LAB_UPDATE')")
    // get all test parameter which is not using for current test templates
    @PostMapping("/unused")
    public ApiResponse<UnusedTestParameterRes> getUnusedTestParameter(
            @RequestBody UnusedTestParameterReq unusedTestParameterReq
            ) {
        return ApiResponse.add("Get unused test parameters successfully",
                testParameterService.getUnusedTestParameter(unusedTestParameterReq));
    }

}
