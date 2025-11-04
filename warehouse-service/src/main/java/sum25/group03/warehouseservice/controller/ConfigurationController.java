package sum25.group03.warehouseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("")
    public ResponseEntity<String> addConfig(@Valid @RequestBody ConfigReq configDTO) {
        configService.createConfig(configDTO);
        return ResponseEntity.ok("Configuration added successfully.");
    }

    @PutMapping("")
    public ResponseEntity<String> updateConfig(@Valid @RequestBody UpdateConfigReq configDTO) {
        configService.updateConfig(configDTO);
        return ResponseEntity.ok("Specific configuration updated successfully.");
    }
    @DeleteMapping("")
    public ResponseEntity<String> deleteById(@RequestParam Long id) {
        configService.deleteById(id);
        return ResponseEntity.ok("Configuration deleted successfully.");
    }

    @GetMapping("")
    public ResponseEntity<?> getAllConfigs(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(configService.getAllConfig(page, size));
    }
    @GetMapping("search")
    public ApiResponse<?> searchConfigs(
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String value,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.add("Search results", configService.searchConfigs(key, value, page, size));
    }

}
