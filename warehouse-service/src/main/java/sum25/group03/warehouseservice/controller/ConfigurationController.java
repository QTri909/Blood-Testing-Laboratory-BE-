package sum25.group03.warehouseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateConfigReq;
import sum25.group03.warehouseservice.service.config.ConfigService;

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
    public ResponseEntity<?> getAllConfigs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(configService.getAllConfig(page, size));
    }
    @GetMapping("search")
    public ResponseEntity<?> searchConfigs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(configService.searchConfigs(keyword, id, page, size));
    }
}
