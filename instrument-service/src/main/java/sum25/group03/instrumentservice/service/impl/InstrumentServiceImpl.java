package sum25.group03.instrumentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.client.WarehouseServiceClient;
import sum25.group03.instrumentservice.client.response.ReagentValidationResponse;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.common.InstrumentStatus;
import sum25.group03.instrumentservice.controller.request.ChangeInstrumentModeRequest;
import sum25.group03.instrumentservice.controller.request.CreateInstrumentRequest;
import sum25.group03.instrumentservice.controller.request.InstallReagentRequest;
import sum25.group03.instrumentservice.controller.response.ChangeInstrumentModeResponse;
import sum25.group03.instrumentservice.controller.response.InstallReagentResponse;
import sum25.group03.instrumentservice.controller.response.InstrumentResponse;
import sum25.group03.instrumentservice.exception.InstrumentModeChangeException;
import sum25.group03.instrumentservice.exception.ResourceNotFoundException;
import sum25.group03.instrumentservice.exception.WarehouseServiceException;
import sum25.group03.instrumentservice.event.ReagentInstalledEvent;

import sum25.group03.instrumentservice.model.Configuration;
import sum25.group03.instrumentservice.model.InstalledReagent;
import sum25.group03.instrumentservice.model.Instrument;
import sum25.group03.instrumentservice.repository.ConfigurationRepository;
import sum25.group03.instrumentservice.repository.InstalledReagentRepository;
import sum25.group03.instrumentservice.repository.InstrumentRepository;
import sum25.group03.instrumentservice.service.InstrumentService;
import sum25.group03.instrumentservice.service.KafkaEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentServiceImpl implements InstrumentService {
    private final InstrumentRepository instrumentRepository;
    private final ConfigurationRepository configurationRepository;
    private final WarehouseServiceClient warehouseServiceClient;
    private final InstalledReagentRepository installedReagentRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public InstrumentResponse createInstrument(CreateInstrumentRequest request) {

        Configuration configuration = configurationRepository.findById(request.getConfigurationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Configuration not found with id: " + request.getConfigurationId()));


        Instrument instrument = Instrument.builder()
                .instrumentCode(request.getInstrumentCode())
                .instrumentName(request.getInstrumentName())
                .status(request.getStatus())
                .configuration(configuration)
                .build();


        Instrument savedInstrument = instrumentRepository.save(instrument);
        return mapToResponse(savedInstrument);
    }

    @Override
    public ChangeInstrumentModeResponse changeInstrumentMode(ChangeInstrumentModeRequest request) {
        log.info("Starting change instrument mode process for instrument ID: {}", request.getInstrumentId());


        Instrument instrument = instrumentRepository.findById(request.getInstrumentId())
                .orElseThrow(() -> {
                    log.error("Instrument not found with ID: {}", request.getInstrumentId());
                    return new ResourceNotFoundException(
                            "Instrument not found with id: " + request.getInstrumentId());
                });

        log.info("Instrument found: {} ({})", instrument.getInstrumentName(), instrument.getInstrumentCode());


        log.info("Checking instrument status with Warehouse Service");
        boolean isActive;
        try {
            isActive = warehouseServiceClient.checkInstrumentStatus(request.getInstrumentId());
        } catch (WarehouseServiceException e) {
            log.error("Warehouse Service check failed: {}", e.getMessage());
            throw new InstrumentModeChangeException(
                    "Cannot change instrument mode: Unable to verify instrument status with Warehouse Service. " + e.getMessage());
        }


        if (!isActive) {
            log.warn("Mode change denied - Instrument is not active in Warehouse Service");
            throw new InstrumentModeChangeException(
                    "Cannot change instrument mode: Instrument is not active in the Warehouse Service. " +
                            "Please ensure the instrument is marked as active before attempting mode changes.");
        }

        log.info("Warehouse Service confirmed instrument is active - proceeding with mode change");


        validateModeChangeRequest(request, instrument);

        InstrumentStatus previousStatus = instrument.getStatus();
        instrument.setStatus(request.getNewStatus());
        Instrument updatedInstrument = instrumentRepository.save(instrument);

        log.info("Instrument mode changed successfully from {} to {}", previousStatus, request.getNewStatus());


        return ChangeInstrumentModeResponse.builder()
                .instrumentId(updatedInstrument.getId())
                .instrumentCode(updatedInstrument.getInstrumentCode())
                .instrumentName(updatedInstrument.getInstrumentName())
                .previousStatus(previousStatus)
                .newStatus(updatedInstrument.getStatus())
                .reason(request.getReason())
                .changedAt(LocalDateTime.now())
                .message("Instrument mode changed successfully from " + previousStatus + " to " + request.getNewStatus())
                .build();
    }

    @Override
    public InstallReagentResponse installReagent(InstallReagentRequest request) {
        log.info("[v0] Starting reagent installation process for instrument ID: {}", request.getInstrumentId());

        Instrument instrument = instrumentRepository.findById(request.getInstrumentId())
                .orElseThrow(() -> {
                    log.error("[v0] Instrument not found with ID: {}", request.getInstrumentId());
                    return new ResourceNotFoundException(
                            "Instrument not found with id: " + request.getInstrumentId());
                });

        log.info("Instrument found: {} ({})", instrument.getInstrumentName(), instrument.getInstrumentCode());


        log.info("Validating reagent with Warehouse Service - batch number: {}", request.getLotNumber());
        ReagentValidationResponse reagentValidation;
        try {
            reagentValidation = warehouseServiceClient.validateReagent(request.getLotNumber(), request.getCurrentVolume());
        } catch (WarehouseServiceException e) {
            log.error("[v0] Warehouse Service validation failed: {}", e.getMessage());
            throw new WarehouseServiceException(
                    "Cannot install reagent: Unable to validate with Warehouse Service. " + e.getMessage());
        }


        if (!reagentValidation.isValid()) {
            log.warn("Reagent validation failed: {}", reagentValidation.getMessage());
            throw new InstrumentModeChangeException(
                    "Cannot install reagent: " + reagentValidation.getMessage());
        }

        log.info("Reagent validation successful - reagent is valid and ready for use");

        if (request.getCurrentVolume() == null || request.getCurrentVolume() <= 0) {
            log.warn("Invalid current volume: {}", request.getCurrentVolume());
            throw new InstrumentModeChangeException("Current volume must be greater than 0");
        }


        InstalledReagent installedReagent = InstalledReagent.builder()
                .instrument(instrument)
                .currentVolume(request.getCurrentVolume())
                .status(InstalledReagentStatus.AVAILABLE)
                .installationDate(LocalDate.now())
                .lotReagentId(reagentValidation.getReagentId().intValue())
                .build();

        InstalledReagent savedReagent = installedReagentRepository.save(installedReagent);

        log.info("[v0] Reagent installed successfully - ID: {}, Batch: {}",
                savedReagent.getId(), request.getLotNumber());

        try {
            ReagentInstalledEvent event = ReagentInstalledEvent.builder()
                    .reagentId(reagentValidation.getReagentId())
                    .reagentName(reagentValidation.getReagentName())
                    .batchNumber(request.getLotNumber())
                    .requiredVolume(request.getCurrentVolume())
                    .instrumentId(instrument.getId())
                    .instrumentName(instrument.getInstrumentName())
                    .installationDate(LocalDate.now())
                    .eventTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .build();

            kafkaEventPublisher.publishReagentInstalledEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish reagent installed event, but reagent installation was successful: {}", e.getMessage());

        }

        return InstallReagentResponse.builder()
                .installedReagentId(savedReagent.getId())
                .instrumentId(instrument.getId())
                .instrumentName(instrument.getInstrumentName())
                .reagentName(reagentValidation.getReagentName())
                .batchNumber(request.getLotNumber())
                .currentVolume(request.getCurrentVolume())
                .installationDate(savedReagent.getInstallationDate())
                .status(InstalledReagentStatus.AVAILABLE)
                .message("Reagent installed successfully and is available for operational use")
                .success(true)
                .build();
    }



    private void validateModeChangeRequest(ChangeInstrumentModeRequest request, Instrument instrument) {
        InstrumentStatus newStatus = request.getNewStatus();
        InstrumentStatus currentStatus = instrument.getStatus();

        log.info("Validating mode change from {} to {}", currentStatus, newStatus);


        if (currentStatus == newStatus) {
            log.warn("Mode change validation failed - same status");
            throw new InstrumentModeChangeException(
                    "Instrument is already in " + newStatus + " mode. No change needed.");
        }


        if ((newStatus == InstrumentStatus.MAINTENANCE || newStatus == InstrumentStatus.INACTIVE) &&
                (request.getReason() == null || request.getReason().trim().isEmpty())) {
            log.warn("Mode change validation failed - missing reason for {} mode", newStatus);
            throw new InstrumentModeChangeException(
                    "Reason is required when changing to " + newStatus + " mode.");
        }


        if (newStatus == InstrumentStatus.READY &&
                (request.getQcCheckDetails() == null || request.getQcCheckDetails().trim().isEmpty())) {
            log.warn("Mode change validation failed - missing QC check details for READY mode");
            throw new InstrumentModeChangeException(
                    "QC check details are required when changing to READY mode.");
        }

        log.info("Mode change validation passed");
    }

    private InstrumentResponse mapToResponse(Instrument instrument) {
        return InstrumentResponse.builder()
                .id(instrument.getId())
                .instrumentCode(instrument.getInstrumentCode())
                .instrumentName(instrument.getInstrumentName())
                .status(instrument.getStatus())
                .configurationId(instrument.getConfiguration().getId())
                .configurationName(instrument.getConfiguration().getConfigKey())
                .build();
    }
}
