package sum25.group03.warehouseservice.service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.dto.request.GlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.SpecificConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateGlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateSpecificConfigReq;
import sum25.group03.warehouseservice.entity.Configuration;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.ConfigMapper;
import sum25.group03.warehouseservice.repository.GlobalConfigRepo;
import sum25.group03.warehouseservice.repository.SpecificConfigRepo;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigServiceImpl implements ConfigService {
    private final SpecificConfigRepo specificConfigRepo;
    private final GlobalConfigRepo globalConfigRepo;
    private final ConfigMapper configMapper;
    @Lazy
    @Autowired
    private InstrumentService instrumentService;
    @Override
    public ConfigurationDTO findByInstrumentId(Long id) {
        return specificConfigRepo.findByInstrumentId(id);
    }

    @Override
    public boolean existsById(Long id) {
        return specificConfigRepo.existsById(id);
    }

    @Override
    public void createGlobalConfig(GlobalConfigReq configReq) {
        GlobalConfiguration config = configMapper.toEntity(configReq);
        globalConfigRepo.save(config);
        log.info("Created new global configuration with id: {}", config.getGlobalConfigurationId());
    }

    @Override
    public void createSpecificConfig(SpecificConfigReq config) {
        GlobalConfiguration globalConfiguration = globalConfigRepo.findById(config.getGlobalConfigurationId()).orElseThrow(
                () -> new NotFoundException("Global Configuration not found with id: " + config.getGlobalConfigurationId())
        );
        Configuration configuration = configMapper.toEntity(config);
        configuration.setGlobalConfiguration(globalConfiguration);
        specificConfigRepo.save(configuration);
        log.info("Created new specific configuration with id: {}", configuration.getSpecificConfigurationId());
    }

    @Override
    public void updateGlobalConfig(UpdateGlobalConfigReq configReq) {
        GlobalConfiguration config = globalConfigRepo.findById(configReq.getGlobalConfigurationId())
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + configReq.getGlobalConfigurationId()));

        config.setSampleVolume(configReq.getSampleVolume());
        config.setSampleVolumeUnit(configReq.getSampleVolumeUnit());
        config.setMaxConcurrentSamples(configReq.getMaxConcurrentSamples());
        config.setDefaultTimeout(configReq.getDefaultTimeout());
        globalConfigRepo.save(config);
        log.info("Updated global configuration with id: {}", config.getGlobalConfigurationId());
    }

    @Override
    public void updateSpecificConfig(UpdateSpecificConfigReq config) {
        Configuration existingConfig = specificConfigRepo.findById(config.getSpecificConfigurationId())
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + config.getSpecificConfigurationId()));
        existingConfig.setSupportedTests(config.getSupportedTests());
        existingConfig.setParameterSettings(config.getParameterSettings());
        existingConfig.setDataOutputFormat(config.getDataOutputFormat());
        existingConfig.setCommunicationProtocol(config.getCommunicationProtocol());
        existingConfig.setMixingSpeed(config.getMixingSpeed());
        existingConfig.setFirmwareVersion(config.getFirmwareVersion());
        specificConfigRepo.save(existingConfig);
        log.info("Updated specific configuration with id: {}", existingConfig.getSpecificConfigurationId());
    }

    @Override
    public void deleteSpecificById(Long id) {
        Configuration config = specificConfigRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + id));
        config.setActive(false);
        specificConfigRepo.save(config);
        log.info("Specific configuration with id {} has been deactivated.", id);
    }

    @Override
    public void deleteGlobalById(Long id) {
        GlobalConfiguration config = globalConfigRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + id));
        config.setActive(false);
        globalConfigRepo.save(config);
        log.info("Global configuration with id {} has been deactivated.", id);

    }

    @Override
    public Page<GlobalConfiguration> getAllGlobalConfig(int page, int size) {
        return globalConfigRepo.findAllByActiveTrue(PageRequest.of(page, size));
    }

    @Override
    public Page<Configuration> getAllSpecificConfig(int page, int size) {
        return specificConfigRepo.findAllByActiveTrue(PageRequest.of(page, size));
    }

    @Override
    public SpecificConfiguration getSpecificConfigByInstrumentId(Long id) {
        return specificConfigRepo.findByInstrumentIdWithGlobalConfig(id);
    }

    @Override
    public GlobalConfiguration getGlobalConfigById(Long id) {
        return null;
    }
}
