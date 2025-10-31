package sum25.group03.warehouseservice.service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateConfigReq;
import sum25.group03.warehouseservice.dto.response.ConfigRes;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.entity.Configuration;
import sum25.group03.warehouseservice.event.ConfigEvent;
import sum25.group03.warehouseservice.event.DeleteConfigEvent;
import sum25.group03.warehouseservice.event.UpdateConfigEvent;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.ConfigMapper;
import sum25.group03.warehouseservice.repository.ConfigRepo;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigServiceImpl implements ConfigService {
    private final ConfigRepo configRepo;
    private final ConfigMapper configMapper;
    private final KafkaTemplate<String, UpdateConfigEvent> kafkaUpdateTemplate;
    private final KafkaTemplate<String, DeleteConfigEvent> kafkaDeleteTemplate;

    @Override
    public boolean existsById(Long id) {
        return configRepo.existsById(id);
    }

    @Override
    public void createConfig(ConfigReq config) {
        Configuration configuration = configMapper.toEntity(config);
        configRepo.save(configuration);
        log.info("Created new specific configuration with id: {}", configuration.getConfigurationId());
    }

    @Override
    public void updateConfig(UpdateConfigReq config) {
        Configuration existingConfig = configRepo.findById(config.getConfigurationId())
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + config.getConfigurationId()));
        configMapper.updateEntityFromDto(config,existingConfig);
        configRepo.save(existingConfig);
        log.info("Updated configuration with id: {}", existingConfig.getConfigurationId());

        // Send update event to Kafka
        ConfigEvent configEvent = ConfigEvent.builder()
                .communicationProtocol(existingConfig.getCommunicationProtocol())
                .dataOutputFormat(existingConfig.getDataOutputFormat())
                .mixingSpeed(existingConfig.getMixingSpeed())
                .supportedTests(existingConfig.getSupportedTests())
                .firmwareVersion(existingConfig.getFirmwareVersion())
                .build();
        UpdateConfigEvent updateConfigEvent = UpdateConfigEvent.builder()
                .instrumentId(existingConfig.getInstrument().getInstrumentId())
                .configEvent(configEvent)
                .build();
        kafkaUpdateTemplate.send("config-updates", updateConfigEvent);
        log.info("Sent update event for configuration id: {} with instrumentId {}", existingConfig.getConfigurationId(), existingConfig.getInstrument().getInstrumentId());
    }

    @Override
    public void deleteById(Long id) {
        Configuration config = configRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Configuration not found with id: " + id));
        config.setActive(false);
        configRepo.save(config);
        log.info("Configuration with id {} has been deactivated.", id);

        // Send delete event to Kafka
        DeleteConfigEvent deleteConfigEvent = DeleteConfigEvent.builder()
                .instrumentId(config.getInstrument().getInstrumentId())
                .build();
        kafkaDeleteTemplate.send("config-deletes", deleteConfigEvent);
        log.info("Sent delete event for configuration id: {} with instrumentId {}", config.getConfigurationId(), config.getInstrument().getInstrumentId());
    }


    @Override
    public PageRes<ConfigRes> getAllConfig(int page, int size) {
        Page<Configuration> config = configRepo.findAllByActiveTrue(PageRequest.of(page, size));
        List<ConfigRes> configRes = configMapper.toDto(config.getContent());
        return PageRes.<ConfigRes>builder()
                .content(configRes)
                .pageNumber(config.getNumber())
                .pageSize(config.getSize())
                .totalElements(config.getTotalElements())
                .totalPages(config.getTotalPages())
                .build();
    }

    @Override
    public Configuration getConfigByInstrumentId(Long id) {
        return configRepo.findByInstrumentId(id);
    }

    @Override
    public Configuration getConfigById(Long id) {
        return configRepo.findByConfigId(id);
    }

    @Override
    public PageRes<ConfigRes> searchConfigs(String keyword, String id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Configuration> configs = configRepo.search(keyword, id, pageable);
        List<ConfigRes> content = configMapper.toDto(configs.getContent());

        return PageRes.<ConfigRes>builder()
                .content(content)
                .pageNumber(configs.getNumber())
                .pageSize(configs.getSize())
                .totalElements(configs.getTotalElements())
                .totalPages(configs.getTotalPages())
                .build();
    }

}
