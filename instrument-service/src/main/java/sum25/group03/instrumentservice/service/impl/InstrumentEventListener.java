package sum25.group03.instrumentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.events.DeleteConfigEvent;
import sum25.group03.common.response.events.NewInstrumentEvent;
import sum25.group03.common.response.events.UpdateConfigEvent;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.common.InstrumentStatus;

import sum25.group03.instrumentservice.exception.ResourceNotFoundException;
import sum25.group03.instrumentservice.model.Configuration;
import sum25.group03.instrumentservice.model.InstalledReagent;
import sum25.group03.instrumentservice.model.Instrument;
import sum25.group03.instrumentservice.repository.InstrumentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentEventListener {
    private final InstrumentRepository instrumentRepository;

    @KafkaListener(topics = "new-instrument-events", groupId = "instrument-service-group", containerFactory = "newInstrumentListenerContainerFactory")
    public void handleNewInstrumentEvent(NewInstrumentEvent newInstrumentEvent) {
        Instrument newInstrument = instrumentRepository.findById(newInstrumentEvent.getInstrumentId())
                .orElseGet(() -> {
                    Instrument i = new Instrument();
                    i.setId(newInstrumentEvent.getInstrumentId());
                    i.setInstrumentName(newInstrumentEvent.getInstrumentName());
                    i.setStatus(InstrumentStatus.READY);
                    return i;
                });
        Configuration config = null;
        List<InstalledReagent> installedReagents = null;
        if(newInstrumentEvent.getConfigEvent() !=null){
             config = Configuration.builder()
                     .configurationName(newInstrumentEvent.getConfigEvent().getConfigurationName())
                    .communicationProtocol(newInstrumentEvent.getConfigEvent().getCommunicationProtocol())
                    .dataOutputFormat(newInstrumentEvent.getConfigEvent().getDataOutputFormat())
                    .firmwareVersion(newInstrumentEvent.getConfigEvent().getFirmwareVersion())
                    .mixingSpeed(newInstrumentEvent.getConfigEvent().getMixingSpeed())
                    .supportedTests(newInstrumentEvent.getConfigEvent().getSupportedTests())
                    .active(true)
                    .build();
            newInstrument.setConfiguration(config);
        }
        if(newInstrumentEvent.getNewReagentEvents()!=null && !newInstrumentEvent.getNewReagentEvents().isEmpty() ){
            installedReagents = newInstrumentEvent.getNewReagentEvents().stream()
                    .map(reagentEvent -> InstalledReagent.builder()
                            .reagentId(reagentEvent.getReagentId())
                            .reagentName(reagentEvent.getReagentName())
                            .unit(reagentEvent.getUnit())
                            .usageMin(reagentEvent.getUsageMin())
                            .usageMax(reagentEvent.getUsageMax())
                            .instrument(newInstrument)
                            .status(InstalledReagentStatus.AVAILABLE)
                            .build())
                    .toList();
            newInstrument.setInstalledReagents(installedReagents);
        }

        instrumentRepository.save(newInstrument);
        log.info("Instrument {} has been sync", newInstrument.getId());
    }

    @KafkaListener(topics = "config-updates", groupId = "instrument-service-group", containerFactory = "configUpdateListenerContainerFactory")
    public void handleConfigUpdateEvent(UpdateConfigEvent config) {
        Instrument instrument = instrumentRepository.findById(config.getInstrumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Instrument not found with ID: " + config.getInstrumentId()));

        Configuration updatedConfig = instrument.getConfiguration();
        updatedConfig.setConfigurationName(config.getConfigEvent().getConfigurationName());
        updatedConfig.setSupportedTests(config.getConfigEvent().getSupportedTests());
        updatedConfig.setDataOutputFormat(config.getConfigEvent().getDataOutputFormat());
        updatedConfig.setCommunicationProtocol(config.getConfigEvent().getCommunicationProtocol());
        updatedConfig.setMixingSpeed(config.getConfigEvent().getMixingSpeed());
        updatedConfig.setFirmwareVersion(config.getConfigEvent().getFirmwareVersion());
        instrument.setConfiguration(updatedConfig);
        instrumentRepository.save(instrument);
        log.info("Configuration for Instrument {} has been updated", instrument.getId());
    }

    @KafkaListener(topics = "config-deletes", groupId = "instrument-service-group", containerFactory = "configDeleteListenerContainerFactory")
    public void handleConfigDeleteEvent(DeleteConfigEvent deleteConfigEvent) {
        Instrument instrument = instrumentRepository.findById(deleteConfigEvent.getInstrumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Instrument not found with ID: " + deleteConfigEvent.getInstrumentId()));

        instrument.setConfiguration(null);
        instrumentRepository.save(instrument);
        log.info("Configuration for Instrument {} has been deleted", instrument.getId());
    }
}
