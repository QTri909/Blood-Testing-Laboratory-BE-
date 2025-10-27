package sum25.group03.warehouseservice.service.instrument;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.annotation.Configurations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.dto.request.AssignConfigAndReagentReq;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.dto.response.InstrumentConfigReagentRes;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.entity.*;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.event.NewConfigEvent;
import sum25.group03.warehouseservice.event.NewInstrumentEvent;
import sum25.group03.warehouseservice.event.NewReagentEvent;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.InstrumentMapper;
import sum25.group03.warehouseservice.repository.InstrumentRepo;
import sum25.group03.warehouseservice.service.config.ConfigService;
import sum25.group03.warehouseservice.service.reagent.ReagentService;
import sum25.group03.warehouseservice.service.reagentusage.ReagentUsageService;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentServiceImpl implements InstrumentService {
    private final InstrumentRepo instrumentRepo;
    private final InstrumentMapper instrumentMapper;

    private final ConfigService configService;
    private final ReagentService reagentService;
    private final ReagentUsageService reagentUsageService;
    private final KafkaTemplate<String, NewInstrumentEvent> kafkaTemplate;

    public boolean isDuplicateSerialNumber(String serialNumber) {
        return instrumentRepo.existsBySerialNumber(serialNumber);
    }
    @Override
    public void addInstrumentToWarehouse(InstrumentReq instrument) {
        Instrument newInstrument = instrumentMapper.toEntity(instrument);
        // Check an instrument serial number duplication
        if(isDuplicateSerialNumber(newInstrument.getSerialNumber())) {
            throw new NotFoundException("Instrument with serial number " + newInstrument.getSerialNumber() + " already exists");
        }
        NewConfigEvent configEvent = null;
        List<NewReagentEvent> reagentEvents = null;
        // Clone from an existing instrument
        if(instrument.getCloneFromInstrumentId() != null) {
            //getConfigAndReagentByInstrument(newInstrument, instrument.getCloneFromInstrumentId());
            SpecificConfiguration specificConfiguration = configService.getSpecificConfigByInstrumentId(instrument.getCloneFromInstrumentId());
            if(specificConfiguration == null){
                log.info("No configuration found to clone for instrument id: {}", instrument.getCloneFromInstrumentId());
            }else{
                SpecificConfiguration newSpecificConfig = SpecificConfiguration.builder()
                        .parameterSettings(specificConfiguration.getParameterSettings())
                        .supportedTests(specificConfiguration.getSupportedTests())
                        .dataOutputFormat(specificConfiguration.getDataOutputFormat())
                        .communicationProtocol(specificConfiguration.getCommunicationProtocol())
                        .mixingSpeed(specificConfiguration.getMixingSpeed())
                        .firmwareVersion(specificConfiguration.getFirmwareVersion())
                        .active(true)
                        .globalConfiguration(specificConfiguration.getGlobalConfiguration())
                        .build();
                newInstrument.setSpecificConfiguration(newSpecificConfig);
                configEvent = NewConfigEvent.builder()
                        .sampleVolume(newSpecificConfig.getGlobalConfiguration().getSampleVolume())
                        .sampleVolumeUnit(newSpecificConfig.getGlobalConfiguration().getSampleVolumeUnit())
                        .maxConcurrentSamples(newSpecificConfig.getGlobalConfiguration().getMaxConcurrentSamples())
                        .defaultTimeout(newSpecificConfig.getGlobalConfiguration().getDefaultTimeout())
                        .supportedTests(newSpecificConfig.getSupportedTests())
                        .parameterSettings(newSpecificConfig.getParameterSettings())
                        .dataOutputFormat(newSpecificConfig.getDataOutputFormat())
                        .communicationProtocol(newSpecificConfig.getCommunicationProtocol())
                        .mixingSpeed(newSpecificConfig.getMixingSpeed())
                        .firmwareVersion(newSpecificConfig.getFirmwareVersion())
                        .build();
            }

            List<Reagents> reagentUsages = reagentService.findAllByInstrumentId(instrument.getCloneFromInstrumentId());
            if(reagentUsages.isEmpty()) {
                log.info("No reagents found to clone for instrument id: {}", instrument.getCloneFromInstrumentId());
            }else{
                List<ReagentHistoryUsage> newReagentUsages = reagentUsages.stream()
                        .map(ru -> ReagentHistoryUsage.builder()
                                .instrument(newInstrument)
                                .reagent(ru)
                                .build())
                        .toList();
                newInstrument.setReagentHistoryUsages(newReagentUsages);
                reagentEvents = reagentUsages.stream()
                        .map(ru -> NewReagentEvent.builder()
                                .reagentId(ru.getReagentId())
                                .reagentName(ru.getReagentName())
                                .build())
                        .toList();
            }
        }
        // Save instrument with reagents and config if exist
        Instrument saveInstrument = instrumentRepo.save(newInstrument);
        log.info("New instrument added to warehouse: {}", newInstrument.getInstrumentName());
        // Publish event
        NewInstrumentEvent event = NewInstrumentEvent.builder()
                .instrumentId(saveInstrument.getInstrumentId())
                .instrumentName(saveInstrument.getInstrumentName())
                .newConfigEvent(configEvent)
                .newReagentEvents(reagentEvents)
                .build();
        kafkaTemplate.send("new-instrument-events", event);
        log.info("Published Kafka event: {}", event);
    }

    @Override
    public InstrumentConfigReagentRes addConfigAndReagentToInstrument(AssignConfigAndReagentReq assignConfigAndReagentReq) {
        // Validate configuration and reagents existence

        return null;
    }


    @Override
    public InstrumentStatusResponse getInstrumentStatus(Long instrumentId) {
        Instrument instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id: " + instrumentId));

        boolean isActive = instrument.getStatus() == InstrumentStatus.ACTIVE;

        return InstrumentStatusResponse.builder()
                .instrumentId(instrument.getInstrumentId())
                .instrumentName(instrument.getInstrumentName())
                .status(instrument.getStatus())
                .isActive(isActive)
                .location(instrument.getLocation())
                .message(isActive ? "Instrument is active and ready for mode change" :
                        "Instrument is not active. Current status: " + instrument.getStatus())
                .build();
    }
}
