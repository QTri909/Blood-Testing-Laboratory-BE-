package sum25.group03.warehouseservice.service.instrument;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.entity.Configurations;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.InstrumentMapper;
import sum25.group03.warehouseservice.repository.InstrumentRepo;
import sum25.group03.warehouseservice.service.configuration.ConfigurationService;
import sum25.group03.warehouseservice.service.reagent.ReagentService;
import sum25.group03.warehouseservice.service.reagentusage.ReagentUsageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "InstrumentService")
public class InstrumentServiceImpl implements InstrumentService {
    private final InstrumentRepo instrumentRepo;
    private final InstrumentMapper instrumentMapper;
    private final ConfigurationService configurationService;
    private final ReagentUsageService reagentUsageService;
    private final ReagentService reagentService;
    @PersistenceContext
    private EntityManager entityManager;
    public boolean isEmptyConfig(Long configId) {
        return configId == null;
    }
    public boolean isEmptyReagents(List<Long> reagentIds) {
        return reagentIds == null || reagentIds.isEmpty();
    }
    public boolean isDuplicateSerialNumber(String serialNumber) {
        return instrumentRepo.existsBySerialNumber(serialNumber);
    }
    public void getConfigAndReagentByInstrument(Instrument instrument, Long cloneFromInstrumentId) {
        ConfigurationDTO configurationDTO = configurationService.findByInstrumentId(cloneFromInstrumentId);
        if (configurationDTO != null) {
            Configurations configRef = mapConfigurations(configurationDTO);
            instrument.setConfiguration(configRef);
        }
        List<Long> reagentIds = reagentUsageService.findIdsByInstrumentId(cloneFromInstrumentId);
        if (!reagentIds.isEmpty()) {
            List<ReagentHistoryUsage> reagentUsages = getReagentUsageReferences(
                    reagentIds,
                    instrument
            );
            instrument.setReagentHistoryUsages(reagentUsages);
        }
    }
    public Configurations mapConfigurations (ConfigurationDTO config) {
        return Configurations.builder()
                .configurationKey(config.getConfigurationKey())
                .configurationValue(config.getConfigurationValue())
                .configurationCategory(config.getConfigurationCategory())
                .instrumentType(config.getInstrumentType())
                .unit(config.getUnit())
                .description(config.getDescription())
                .active(config.isActive())
                .build();
    }
    public List<ReagentHistoryUsage> getReagentUsageReferences(List<Long> reagentIds, Instrument instrument) {
        return reagentIds.stream()
                .map(id -> {
                    Reagents reagentRef = Reagents.builder().reagentId(id).build();
                    return ReagentHistoryUsage.builder()
                            .instrument(instrument)
                            .reagent(reagentRef)
                            .build();
                })
                .toList();
    }
    @Override
    public void addInstrumentToWarehouse(InstrumentReq instrument) {
        Instrument newInstrument = instrumentMapper.toEntity(instrument);
        // Check instrument serial number duplication
        if(isDuplicateSerialNumber(newInstrument.getSerialNumber())) {
            throw new NotFoundException("Instrument with serial number " + newInstrument.getSerialNumber() + " already exists");
        }
        // Clone from existing instrument
        if(instrument.getCloneFromInstrumentId() != null) {
            getConfigAndReagentByInstrument(newInstrument, instrument.getCloneFromInstrumentId());
            instrumentRepo.save(newInstrument);
            return;
        }
        // Validate configuration and reagents existence
        List<String> errorMessages = new ArrayList<>();
        if(!isEmptyConfig(instrument.getConfigurationId())) {
            if(!configurationService.existsById(instrument.getConfigurationId())){
                errorMessages.add("Configuration not found");
            } else{
                // Configuration exists
                // Link configuration (as reference)
                Configurations configRef = entityManager.getReference(Configurations.class, instrument.getConfigurationId());
                newInstrument.setConfiguration(configRef);
            }
        }
        if(!isEmptyReagents(instrument.getReagentId())) {
            List<Long> existingReagentIds = reagentService.findExistingIds(instrument.getReagentId());
            List<Long> missingReagentIds = instrument.getReagentId().stream()
                    .filter(id -> !existingReagentIds.contains(id))
                    .toList();

            if (!missingReagentIds.isEmpty()){
                errorMessages.add("Reagents not found: " + missingReagentIds);
            } else {
                // Create reagent usages
                List<ReagentHistoryUsage> usages = getReagentUsageReferences(instrument.getReagentId(), newInstrument);
                newInstrument.setReagentHistoryUsages(usages);
            }
        }
        if(!errorMessages.isEmpty()) {
            throw new NotFoundException(String.join(". ", errorMessages));
        }
        // Save instrument with reagents and config if exist
        instrumentRepo.save(newInstrument);
    }

    @Override
    public void activateInstrument(Long id, String username) {
        Instrument instrument = instrumentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Instrument not found"));
        instrument.setStatus(InstrumentStatus.READY);
        instrument.setUpdatedAt(LocalDate.now());
        instrumentRepo.save(instrument);

        log.info("User '{}' activated instrument with id {}", username, id);
    }

    @Override
    public void deactivateInstrument(Long id, String username) {
        Instrument instrument = instrumentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Instrument not found"));
        instrument.setStatus(InstrumentStatus.DELETED);
        instrument.setUpdatedAt(LocalDate.now());
        instrumentRepo.save(instrument);

        log.info("User '{}' deactivated instrument with id {}", username, id);
    }

}

