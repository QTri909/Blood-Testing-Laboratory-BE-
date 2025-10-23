package sum25.group03.warehouseservice.service.config;

import lombok.RequiredArgsConstructor;
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
import sum25.group03.warehouseservice.entity.GlobalConfiguration;
import sum25.group03.warehouseservice.entity.SpecificConfiguration;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.ConfigType;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.ConfigMapper;
import sum25.group03.warehouseservice.repository.GlobalConfigRepo;
import sum25.group03.warehouseservice.repository.SpecificConfigRepo;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
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
    }

    @Override
    public void createSpecificConfig(SpecificConfigReq config) {
        GlobalConfiguration globalConfiguration = globalConfigRepo.findById(config.getGlobalConfigurationId()).orElseThrow(
                () -> new NotFoundException("Global Configuration not found with id: " + config.getGlobalConfigurationId())
        );
        SpecificConfiguration specificConfiguration = configMapper.toEntity(config);
        specificConfiguration.setGlobalConfiguration(globalConfiguration);
        specificConfigRepo.save(specificConfiguration);
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
    }

    @Override
    public void updateSpecificConfig(UpdateSpecificConfigReq config) {
        SpecificConfiguration existingConfig = specificConfigRepo.findById(config.getSpecificConfigurationId())
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + config.getSpecificConfigurationId()));
        existingConfig.setSupportedTests(config.getSupportedTests());
        existingConfig.setParameterSettings(config.getParameterSettings());
        existingConfig.setDataOutputFormat(config.getDataOutputFormat());
        existingConfig.setCommunicationProtocol(config.getCommunicationProtocol());
        existingConfig.setMixingSpeed(config.getMixingSpeed());
        existingConfig.setFirmwareVersion(config.getFirmwareVersion());
        specificConfigRepo.save(existingConfig);
    }

    @Override
    public void deleteSpecificById(Long id) {
        SpecificConfiguration config = specificConfigRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + id));
        config.setActive(false);
        specificConfigRepo.save(config);
    }

    @Override
    public void deleteGlobalById(Long id) {
        GlobalConfiguration config = globalConfigRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + id));
        config.setActive(false);
        globalConfigRepo.save(config);
    }

    @Override
    public Page<GlobalConfiguration> getAllGlobalConfig(int page, int size) {
        return globalConfigRepo.findAllByActiveTrue(PageRequest.of(page, size));
    }

    @Override
    public Page<SpecificConfiguration> getAllSpecificConfig(int page, int size) {
        return specificConfigRepo.findAllByActiveTrue(PageRequest.of(page, size));
    }
}
