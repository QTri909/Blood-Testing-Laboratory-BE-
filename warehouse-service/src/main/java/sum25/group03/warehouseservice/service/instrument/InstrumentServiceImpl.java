package sum25.group03.warehouseservice.service.instrument;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.annotation.Configurations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.request.AssignConfigAndReagentReq;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.dto.response.*;
import sum25.group03.warehouseservice.entity.*;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.event.ConfigEvent;
import sum25.group03.warehouseservice.event.NewInstrumentEvent;
import sum25.group03.warehouseservice.event.NewReagentEvent;
import sum25.group03.warehouseservice.exception.MissingRequiredFieldsException;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.ConfigMapper;
import sum25.group03.warehouseservice.mapper.InstrumentMapper;
import sum25.group03.warehouseservice.mapper.ReagentMapper;
import sum25.group03.warehouseservice.repository.InstrumentRepo;
import sum25.group03.warehouseservice.service.config.ConfigService;
import sum25.group03.warehouseservice.service.reagent.ReagentService;
import sum25.group03.warehouseservice.service.reagentusage.ReagentUsageService;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentServiceImpl implements InstrumentService {
    private final InstrumentRepo instrumentRepo;
    private final InstrumentMapper instrumentMapper;
    private final ConfigMapper configMapper;
    private final ReagentMapper reagentMapper;
    private final ConfigService configService;
    private final ReagentService reagentService;
    private final ReagentUsageService reagentUsageService;
    private final KafkaTemplate<String, NewInstrumentEvent> kafkaTemplate;


    public boolean isDuplicateSerialNumber(String serialNumber) {
        return instrumentRepo.existsBySerialNumber(serialNumber);
    }
    public ConfigEvent buildConfigEventFromConfiguration(Configuration configuration) {
        return ConfigEvent.builder()
                .configurationName(configuration.getConfigurationName())
                .supportedTests(configuration.getSupportedTests())
                .dataOutputFormat(configuration.getDataOutputFormat())
                .communicationProtocol(configuration.getCommunicationProtocol())
                .mixingSpeed(configuration.getMixingSpeed())
                .firmwareVersion(configuration.getFirmwareVersion())
                .build();
    }
    public List<ReagentHistoryUsage> buildReagentHistoryUsagesFromReagents(Instrument instrument, List<Reagents> reagents) {
        return reagents.stream()
                .map(r -> ReagentHistoryUsage.builder()
                        .instrument(instrument)
                        .reagent(r)
                        .build())
                .toList();
    }
    public List<NewReagentEvent> buildNewReagentEventsFromReagents(List<Reagents> reagents) {
        return reagents.stream()
                .map(r -> NewReagentEvent.builder()
                        .reagentId(r.getReagentId())
                        .reagentName(r.getReagentName())
                        .build())
                .toList();
    }
    @Override
    public InstrumentResponse addInstrumentToWarehouse(InstrumentReq instrument) {
        Instrument newInstrument = instrumentMapper.toEntity(instrument);
        // Check an instrument serial number duplication
        if(isDuplicateSerialNumber(newInstrument.getSerialNumber())) {
            throw new NotFoundException("Instrument with serial number " + newInstrument.getSerialNumber() + " already exists");
        }
        ConfigEvent configEvent = null;
        List<NewReagentEvent> reagentEvents = null;
        // Clone from an existing instrument
        if(instrument.getCloneFromInstrumentId() != null) {
            Configuration configuration = configService.getConfigByInstrumentId(instrument.getCloneFromInstrumentId());
            if(configuration == null){
                log.info("No configuration found to clone for instrument id: {}", instrument.getCloneFromInstrumentId());
            }else{
                Configuration newConfig = Configuration.builder()
                        .configurationName(configuration.getConfigurationName())
                        .supportedTests(configuration.getSupportedTests())
                        .dataOutputFormat(configuration.getDataOutputFormat())
                        .communicationProtocol(configuration.getCommunicationProtocol())
                        .mixingSpeed(configuration.getMixingSpeed())
                        .firmwareVersion(configuration.getFirmwareVersion())
                        .active(true)
                        .build();
                newInstrument.setConfiguration(newConfig);
                configEvent = buildConfigEventFromConfiguration(newConfig);
            }
            List<Reagents> reagentUsages = reagentService.findAllByInstrumentId(instrument.getCloneFromInstrumentId());
            if(reagentUsages.isEmpty()) {
                log.info("No reagents found to clone for instrument id: {}", instrument.getCloneFromInstrumentId());
            }else{
                List<ReagentHistoryUsage> newReagentUsages = buildReagentHistoryUsagesFromReagents(newInstrument, reagentUsages);
                newInstrument.setReagentHistoryUsages(newReagentUsages);
                reagentEvents = buildNewReagentEventsFromReagents(reagentUsages);
            }
        }
        // Save instrument with reagents and config if exist
        Instrument saveInstrument = instrumentRepo.save(newInstrument);
        log.info("New instrument added to warehouse: {}", newInstrument.getInstrumentName());
        // Publish event
        NewInstrumentEvent event = NewInstrumentEvent.builder()
                .instrumentId(saveInstrument.getInstrumentId())
                .instrumentName(saveInstrument.getInstrumentName())
                .configEvent(configEvent)
                .newReagentEvents(reagentEvents)
                .build();
        kafkaTemplate.send("new-instrument-events", event);
        log.info("Published Kafka event (new-instrument-events): {}", event);
        return InstrumentResponse.builder()
                .instrumentId(saveInstrument.getInstrumentId())
                .instrumentName(saveInstrument.getInstrumentName())
                .model(saveInstrument.getModel())
                .serialNumber(saveInstrument.getSerialNumber())
                .status(saveInstrument.getStatus())
                .createdAt(saveInstrument.getCreatedAt())
                .deactivatedAt(saveInstrument.getDeactivatedAt())
                .build();
    }

    @Override
    public InstrumentConfigReagentRes addConfigAndReagentToInstrument(AssignConfigAndReagentReq assignConfigAndReagentReq) {
        // Validate configuration and reagents existence
        if(assignConfigAndReagentReq.getConfigurationId() == null && assignConfigAndReagentReq.getReagentIds().isEmpty()){
            throw new MissingRequiredFieldsException("No configuration and reagents provided to assign");
        }
        Instrument instrument = instrumentRepo.findById(assignConfigAndReagentReq.getInstrumentId())
                .orElseThrow(() -> new NotFoundException("Instrument not found with id: " + assignConfigAndReagentReq.getInstrumentId()));
        Configuration config = configService.getConfigById(assignConfigAndReagentReq.getConfigurationId());

        ConfigEvent configEvent = null;
        List<NewReagentEvent> reagentEvents = null;
        List<ReagentForInstrumentRes> reagentForInstrumentResList = null;
        if(config != null){
            Configuration newConfig = Configuration.builder()
                    .configurationName(config.getConfigurationName())
                    .supportedTests(config.getSupportedTests())
                    .dataOutputFormat(config.getDataOutputFormat())
                    .communicationProtocol(config.getCommunicationProtocol())
                    .mixingSpeed(config.getMixingSpeed())
                    .firmwareVersion(config.getFirmwareVersion())
                    .active(true)
                    .build();
            instrument.setConfiguration(newConfig);
            configEvent = buildConfigEventFromConfiguration(config);
        }

        List<Reagents> reagents = reagentService.findAllByReagentId(assignConfigAndReagentReq.getReagentIds());
        if(!reagents.isEmpty()){
            List<ReagentHistoryUsage> reagentHistoryUsages = buildReagentHistoryUsagesFromReagents(instrument, reagents);
            instrument.setReagentHistoryUsages(reagentHistoryUsages);
            reagentEvents = buildNewReagentEventsFromReagents(reagents);
            reagentForInstrumentResList = reagents.stream()
                    .map(r -> ReagentForInstrumentRes.builder()
                            .reagentId(r.getReagentId())
                            .reagentName(r.getReagentName())
                            .catalogNumber(r.getCatalogNumber())
                            .casNumber(r.getCasNumber())
                            .build())
                    .toList();
        }
        Instrument savedInstrument = instrumentRepo.save(instrument);
        log.info("Assigned configuration and reagents to instrument id: {}", instrument.getInstrumentId());

        NewInstrumentEvent event = NewInstrumentEvent.builder()
                .instrumentId(savedInstrument.getInstrumentId())
                .instrumentName(savedInstrument.getInstrumentName())
                .configEvent(configEvent)
                .newReagentEvents(reagentEvents)
                .build();
        kafkaTemplate.send("config-updates", event);
        log.info("Published Kafka event (update instrument): {}", event);

        return InstrumentConfigReagentRes.builder()
                .instrumentId(savedInstrument.getInstrumentId())
                .instrumentName(savedInstrument.getInstrumentName())
                .model(savedInstrument.getModel())
                .serialNumber(savedInstrument.getSerialNumber())
                //.location(savedInstrument.getLocation())
                .notes(savedInstrument.getNotes()!=null? savedInstrument.getNotes() : "")
                .status(savedInstrument.getStatus())
                .createdAt(savedInstrument.getCreatedAt())
                .updatedAt(savedInstrument.getUpdatedAt())
                .configRes(configMapper.toDto(savedInstrument.getConfiguration()))
                .reagentForInstrumentRes(reagentForInstrumentResList)
                .build();
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

    @Override
    public InstrumentConfigReagentRes getInstrumentById(Long instrumentId) {
        Instrument instrument = instrumentRepo.findInstrumentById(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id: " + instrumentId));
        List<ReagentForInstrumentRes> reagentForInstrumentRes = instrument.getReagentHistoryUsages().stream()
                .map(r -> ReagentForInstrumentRes.builder()
                        .reagentId(r.getReagent().getReagentId())
                        .reagentName(r.getReagent().getReagentName())
                        .quantityUsed(r.getQuantityUsed())
                        .unit(r.getUnit())
                        .lotNumber(r.getLotNumber())
                        .usedAt(r.getUsedAt())
                        .build())
                .toList();
        InstrumentConfigReagentRes response = InstrumentConfigReagentRes.builder()
                .instrumentId(instrument.getInstrumentId())
                .instrumentName(instrument.getInstrumentName())
                .model(instrument.getModel())
                .serialNumber(instrument.getSerialNumber())
                //.location(instrument.getLocation())
                .notes(instrument.getNotes()!=null? instrument.getNotes() : "")
                .status(instrument.getStatus())
                .createdAt(instrument.getCreatedAt())
                .updatedAt(instrument.getUpdatedAt())
                .configRes(configMapper.toDto(instrument.getConfiguration()))
                .reagentForInstrumentRes(reagentForInstrumentRes)
                .build();
        return response;
    }

    @Override
    public PageRes<InstrumentResponse> getAllInstruments(int page, int size, String key) {
        Page<Instrument> pageInstrument = instrumentRepo.searchInstrumentsByName(key, PageRequest.of(page, size));
        List<Instrument> instruments = pageInstrument.getContent();
        Long totalElements = null;
        if(key!=null ){
             totalElements = instruments.stream().count();
        } else
             totalElements = instrumentRepo.count();
        List<InstrumentResponse> instrumentResponses = instruments.stream()
                .map(i -> InstrumentResponse.builder()
                        .instrumentId(i.getInstrumentId())
                        .instrumentName(i.getInstrumentName())
                        .model(i.getModel())
                        .serialNumber(i.getSerialNumber())
                        .status(i.getStatus())
                        .createdAt(i.getCreatedAt())
                        .deactivatedAt(i.getDeactivatedAt())
                        .build())
                .toList();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return PageRes.<InstrumentResponse>builder()
                .content(instrumentResponses)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public List<InstrumentResponse> getList() {
        List<Instrument> instruments = instrumentRepo.findAllByStatusActive();
        return instruments.stream()
                .map(i -> InstrumentResponse.builder()
                        .instrumentId(i.getInstrumentId())
                        .instrumentName(i.getInstrumentName())
                        .model(i.getModel())
                        .serialNumber(i.getSerialNumber())
                        .build())
                .toList();
    }
}
