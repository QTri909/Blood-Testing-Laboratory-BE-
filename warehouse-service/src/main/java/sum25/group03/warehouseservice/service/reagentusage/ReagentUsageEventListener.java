package sum25.group03.warehouseservice.service.reagentusage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.event.ReagentUsageHistoryEvent;
import sum25.group03.warehouseservice.repository.InstrumentRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;
import sum25.group03.warehouseservice.repository.ReagentUsageRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentUsageEventListener {
    private final ReagentUsageRepo reagentUsageRepo;
    private final InstrumentRepo instrumentRepo;
    private final ReagentRepo reagentRepo;

    @KafkaListener(
            topics = "reagent-usage-history-events",
            groupId = "warehouse-service-group",
            containerFactory = "reagentUsageHistoryEventContainerFactory"
    )
    public void handleReagentUsageEvent(@Payload ReagentUsageHistoryEvent event) {
        try {
            log.info("Received Reagent Usage Event: {}", event);
            Instrument instrument = instrumentRepo.findById(event.getInstrumentId())
                    .orElseThrow(() -> new RuntimeException("Instrument not found with ID: " + event.getInstrumentId()));
            Reagents reagent = reagentRepo.findById(event.getReagentId())
                    .orElseThrow(() -> new RuntimeException("Reagent not found with ID: " + event.getReagentId()));
            ReagentHistoryUsage reagentHistoryUsage = getReagentHistoryUsage(event, instrument, reagent);
            reagentUsageRepo.save(reagentHistoryUsage);
            log.info("Reagent Usage History saved successfully for Reagent ID: {} on Instrument ID: {}",
                    event.getReagentId(), event.getInstrumentId());

        } catch (Exception e) {
            log.error("Failed to process Reagent Usage Event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process Reagent Usage Event", e);
        }
    }

    private static ReagentHistoryUsage getReagentHistoryUsage(ReagentUsageHistoryEvent event, Instrument instrument, Reagents reagent) {
        ReagentHistoryUsage reagentHistoryUsage = new ReagentHistoryUsage();
        reagentHistoryUsage.setInstrument(instrument);
        reagentHistoryUsage.setReagent(reagent);
        reagentHistoryUsage.setQuantityUsed(event.getQuantityUsed());
        reagentHistoryUsage.setUnit(event.getUnit());
        reagentHistoryUsage.setUsageType(event.getUsageType());
        reagentHistoryUsage.setTestOrderId(event.getTestOrderId());
        reagentHistoryUsage.setUsedBy(event.getUsedBy());
        reagentHistoryUsage.setUsedAt(event.getUsedAt());
        reagentHistoryUsage.setNotes(event.getNotes());
        reagentHistoryUsage.setLotNumber(event.getLotNumber());
        return reagentHistoryUsage;
    }
}
