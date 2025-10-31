package sum25.group03.testorderservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.testorderservice.dtos.request.SyncedConfigurationDTO;
import sum25.group03.testorderservice.service.impl.SyncedConfigurationServiceImpl;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class SyncedConfigurationController {

    @Autowired
    private SyncedConfigurationServiceImpl syncedConfigurationService;

    @PostMapping("/publish")
    public ResponseEntity<?> publishConfig(@Valid @RequestBody SyncedConfigurationDTO dto){
        syncedConfigurationService.handleConfigUpdate(dto);
        return ResponseEntity.ok("Configuration published successfully.");
    }

}
