package sum25.group03.warehouseservice.service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateGlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateSpecificConfigReq;
import sum25.group03.warehouseservice.entity.Configurations;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.ConfigType;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.ConfigMapper;
import sum25.group03.warehouseservice.repository.ConfigRepo;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    private final ConfigRepo configRepo;
    private final ConfigMapper configMapper;
    @Lazy
    @Autowired
    private InstrumentService instrumentService;
    @Override
    public ConfigurationDTO findByInstrumentId(Long id) {
        return configRepo.findByInstrumentId(id);
    }

    @Override
    public boolean existsById(Long id) {
        return configRepo.existsById(id);
    }

    @Override
    public void createGlobalConfig(ConfigReq configReq) {
        Configurations config = configMapper.toEntity(configReq);
        configRepo.save(config);
    }

    @Override
    public void updateGlobalConfig(UpdateGlobalConfigReq configReq) {
        Configurations config = configRepo.findById(configReq.getConfigurationId())
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + configReq.getConfigurationId()));

        config.setSampleVolume(configReq.getSampleVolume());
        config.setSampleVolumeUnit(configReq.getSampleVolumeUnit());
        config.setDescription(configReq.getDescription());
        config.setMaxConcurrentSamples(configReq.getMaxConcurrentSamples());
        config.setParameterSettings(configReq.getParameterSettings());
        config.setSupportedTests(configReq.getSupportedTests());
        configRepo.save(config);
    }

    @Override
    public void updateSpecificConfig(UpdateSpecificConfigReq config) {
        List<Instrument> instrumentList = new ArrayList<>();
        Instrument instrument = instrumentService.findById(config.getInstrumentId());

        Configurations existingConfig = Configurations.builder()
                .configType(ConfigType.SPECIFIC)
                .sampleVolume(config.getSampleVolume())
                .sampleVolumeUnit(config.getSampleVolumeUnit())
                .description(config.getDescription())
                .maxConcurrentSamples(config.getMaxConcurrentSamples())
                .parameterSettings(config.getParameterSettings())
                .supportedTests(config.getSupportedTests())
                .instrument(instrumentList)
                .build();
        instrument.setConfigurations(existingConfig);
        instrumentList.add(instrument);
        configRepo.save(existingConfig);
    }

    @Override
    public void deleteById(Long id) {
        Configurations config = configRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + id));
        config.setActive(false);
        configRepo.save(config);
    }
}
