package sum25.group03.instrumentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.common.InstrumentStatus;
import sum25.group03.instrumentservice.event.NewInstrumentEvent;
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
        Configuration config = null;
        List<InstalledReagent> installedReagents = null;
        if(newInstrumentEvent.getConfigEvent() !=null){
             config = Configuration.builder()
                    .communicationProtocol(newInstrumentEvent.getConfigEvent().getCommunicationProtocol())
                    .dataOutputFormat(newInstrumentEvent.getConfigEvent().getDataOutputFormat())
                    .firmwareVersion(newInstrumentEvent.getConfigEvent().getFirmwareVersion())
                    .mixingSpeed(newInstrumentEvent.getConfigEvent().getMixingSpeed())
                    .supportedTests(newInstrumentEvent.getConfigEvent().getSupportedTests())
                    .usePerRun(newInstrumentEvent.getConfigEvent().getUsePerRun())
                    .active(true)
                    .build();
        }
        if(!newInstrumentEvent.getNewReagentEvents().isEmpty()){
            installedReagents = newInstrumentEvent.getNewReagentEvents().stream()
                    .map(reagentEvent -> InstalledReagent.builder()
                            .reagentId(reagentEvent.getReagentId())
                            .reagentName(reagentEvent.getReagentName())
                            .build())
                    .toList();
        }

        Instrument instrument = Instrument.builder()
                .id(newInstrumentEvent.getInstrumentId())
                .instrumentName(newInstrumentEvent.getInstrumentName())
                .configuration(config)
                .installedReagents(installedReagents)
                .status(InstrumentStatus.READY)
                .build();
        instrumentRepository.save(instrument);
        log.info("Instrument {} has been sync", instrument.getId());
    }
}
