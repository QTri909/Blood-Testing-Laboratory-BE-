package sum25.group03.warehouseservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.entity.enums.TestType;

@RestController
@RequestMapping("/api/v1/test-parameters")
public class TestParameterController {

    @GetMapping("")
    public ApiResponse testParameter(@RequestBody TestType testType) {
        return ApiResponse.ok("Test parameters endpoint is working");
    }

}
