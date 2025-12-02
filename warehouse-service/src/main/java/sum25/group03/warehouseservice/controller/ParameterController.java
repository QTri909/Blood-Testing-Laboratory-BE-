package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.response.ParameterRes;
import sum25.group03.warehouseservice.service.parameter.ParameterService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parameters")
@RequiredArgsConstructor
public class ParameterController {
    private final ParameterService parameterService;

    @PreAuthorize("hasAuthority('LAB_VIEW')")
    @GetMapping
    public ApiResponse<List<ParameterRes>> getParameters() {
        List<ParameterRes> param = parameterService.getAllParameters();
        return ApiResponse.ok("Get all paramters successfully",param);
    }
    @PreAuthorize("hasAuthority('LAB_VIEW')")
    @GetMapping("/units")
    public ApiResponse<List<String>> getAllParameterUnits() {
        List<String> paramUnits = parameterService.getAllParameterUnits();
        return ApiResponse.ok("Get all parameter units successfully", paramUnits);
    }
}
