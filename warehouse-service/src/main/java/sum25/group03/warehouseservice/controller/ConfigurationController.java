package sum25.group03.warehouseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateConfigReq;
import sum25.group03.warehouseservice.dto.response.ConfigRes;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.service.config.ConfigService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
public class ConfigurationController {
    private final ConfigService configService;


    @PreAuthorize("hasAuthority('LAB_UPDATE') or hasAuthority('CONFIG_UPDATE')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> addConfig(@Valid @RequestBody ConfigReq configDTO) {
        return ApiResponse.ok(configService.createConfig(configDTO));
    }

    @PreAuthorize("hasAuthority('LAB_UPDATE') or hasAuthority('CONFIG_UPDATE')")
    @PutMapping("")
    public ApiResponse<?> updateConfig(@Valid @RequestBody UpdateConfigReq configDTO) {
        return ApiResponse.ok(configService.updateConfig(configDTO));
    }

    @PreAuthorize("hasAuthority('LAB_UPDATE') or hasAuthority('CONFIG_UPDATE')")
    @DeleteMapping("")
    public ApiResponse<?> deleteById(@RequestParam Long id) {
        configService.deleteById(id);
        return ApiResponse.message("Deleted configuration successfully.").build();
    }
    @PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('CONFIG_VIEW')")
    @GetMapping("")
    public ApiResponse<?> getAllConfigs() {
        return ApiResponse.ok(configService.getAllConfig());
    }
    @GetMapping("search")
    public ApiResponse<PageRes<ConfigRes>> searchConfigs(
            @RequestParam(required = false) String key,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.add("Search results", configService.searchConfigs(key, page, size));
    }

}
