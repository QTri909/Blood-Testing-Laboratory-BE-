package sum25.group03.testorderservice.services.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.dto.request.SyncedConfigurationDTO;
import sum25.group03.testorderservice.entity.SyncedConfiguration;
import sum25.group03.testorderservice.enums.SyncedConfigurationStatus;
import sum25.group03.testorderservice.repositories.SyncedConfigurationRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncedConfigurationServiceImpl {

    @Autowired
    private SyncedConfigurationRepository  syncedConfigurationRepository;

    @Autowired
    private ParameterServiceImpl parameterServiceImpl;

    public void handleConfigUpdate(SyncedConfigurationDTO dto){
        parameterServiceImpl.updateParameter(dto);
        SyncedConfiguration config = new SyncedConfiguration();
        config.setConfigKey(dto.getConfigKey());
        config.setMinValue(dto.getMinValue());
        config.setMaxValue(dto.getMaxValue());
        config.setDescription(dto.getDescription());
        config.setUnit(dto.getUnit());
        config.setStatus(SyncedConfigurationStatus.ACTIVE);
        config.setSyncedAt(LocalDateTime.now());
        syncedConfigurationRepository.save(config);
        log.info("✅ Configuration saved/updated in database: {}", config);
    }

}
