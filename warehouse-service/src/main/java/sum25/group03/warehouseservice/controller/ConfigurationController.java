package sum25.group03.warehouseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.request.GlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.SpecificConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateGlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateSpecificConfigReq;
import sum25.group03.warehouseservice.service.config.ConfigService;

@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
public class ConfigurationController {
   private final ConfigService configService;

   @PostMapping("/global")
    public ResponseEntity<String> addGlobalConfig(@Valid @RequestBody GlobalConfigReq configDTO) {
        configService.createGlobalConfig(configDTO);
        return ResponseEntity.ok("Global configuration added successfully.");
   }
    @PostMapping("specific")
    public ResponseEntity<String> addSpecificConfig(@Valid @RequestBody SpecificConfigReq configDTO) {
        configService.createSpecificConfig(configDTO);
        return ResponseEntity.ok("Global configuration added successfully.");
    }
   @PutMapping("/global")
    public ResponseEntity<String> updateGlobalConfig(@Valid @RequestBody UpdateGlobalConfigReq configDTO) {
        configService.updateGlobalConfig(configDTO);
        return ResponseEntity.ok("Global configuration updated successfully.");
   }

   @PutMapping("/specific")
    public ResponseEntity<String> updateSpecificConfig(@Valid @RequestBody UpdateSpecificConfigReq configDTO) {
        configService.updateSpecificConfig(configDTO);
        return ResponseEntity.ok("Specific configuration updated successfully.");
   }
   @DeleteMapping("/specific")
    public ResponseEntity<String> deleteSpecificById(@RequestParam Long id) {
        configService.deleteSpecificById(id);
        return ResponseEntity.ok("Configuration deleted successfully.");
   }
    @DeleteMapping("/global")
    public ResponseEntity<String> deleteGlobalById(@RequestParam Long id) {
        configService.deleteGlobalById(id);
        return ResponseEntity.ok("Configuration deleted successfully.");
    }
    @GetMapping("/global")
    public ResponseEntity<?> getAllGlobalConfigs(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(configService.getAllGlobalConfig(page, size));
    }
    @GetMapping("/specific")
    public ResponseEntity<?> getAllSpecificConfigs(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(configService.getAllSpecificConfig(page, size));
    }

}
