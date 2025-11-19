package sum25.group03.testorderservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.dtos.response.ParameterResponseDTO;
import sum25.group03.testorderservice.services.interfaces.ParameterService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parameters") //  {{api_gateway}}/api/v1/test-orders/parameters
@RequiredArgsConstructor
public class ParameterController {

    private final ParameterService parameterService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ParameterResponseDTO>> getAllParameters() {
        return ApiResponse.add("Get all paramters successfully", parameterService.getAllParameters());
    }
}
