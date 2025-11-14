package sum25.group03.testorderservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.dtos.request.SyncedConfigurationDTO;
import sum25.group03.testorderservice.services.impl.SyncedConfigurationServiceImpl;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class SyncedConfigurationController {

    @Autowired
    private SyncedConfigurationServiceImpl syncedConfigurationService;

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<?> publishConfig(@Valid @RequestBody SyncedConfigurationDTO dto){
        syncedConfigurationService.handleConfigUpdate(dto);
        return ApiResponse.add("Configuration published successfully", null);
    }

}
