package sum25.group03.warehouseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateGlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateSpecificConfigReq;
import sum25.group03.warehouseservice.service.config.ConfigService;

@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
public class ConfigurationController {
   private final ConfigService configService;

   @PostMapping("")
    public ResponseEntity<String> addGlobalConfig(@Valid @RequestBody ConfigReq configDTO) {
        configService.createGlobalConfig(configDTO);
        return ResponseEntity.ok("Global configuration added successfully.");
   }

   @PutMapping("/global")
    public ResponseEntity<String> updateGlobalConfig(@Valid @RequestBody UpdateGlobalConfigReq configDTO) {
        configService.updateGlobalConfig(configDTO);
        return ResponseEntity.ok("Global configuration updated successfully.");
   }

   @PostMapping("/specific")
    public ResponseEntity<String> updateSpecificConfig(@Valid @RequestBody UpdateSpecificConfigReq configDTO) {
        configService.updateSpecificConfig(configDTO);
        return ResponseEntity.ok("Specific configuration updated successfully.");
   }
   @DeleteMapping("")
    public ResponseEntity<String> deleteConfig(@RequestParam Long id) {
        configService.deleteById(id);
        return ResponseEntity.ok("Configuration deleted successfully.");
   }
}
