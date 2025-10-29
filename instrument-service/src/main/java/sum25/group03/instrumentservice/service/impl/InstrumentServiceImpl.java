package sum25.group03.instrumentservice.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sum25.group03.instrumentservice.audit.annotation.SkipAuditLog;
import sum25.group03.instrumentservice.audit.model.AuditLog;
import sum25.group03.instrumentservice.client.WarehouseServiceClient;
import sum25.group03.instrumentservice.client.response.ReagentValidationResponse;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.common.InstrumentStatus;
import sum25.group03.instrumentservice.controller.request.ChangeInstrumentModeRequest;
import sum25.group03.instrumentservice.controller.request.InstallReagentRequest;
import sum25.group03.instrumentservice.controller.request.UpdateReagentStatusRequest;
import sum25.group03.instrumentservice.controller.response.*;
import sum25.group03.instrumentservice.exception.InstrumentModeChangeException;
import sum25.group03.instrumentservice.exception.ResourceNotFoundException;
import sum25.group03.instrumentservice.exception.WarehouseServiceException;
import sum25.group03.instrumentservice.event.InstrumentModeChangedEvent;
import sum25.group03.instrumentservice.event.ReagentInstalledEvent;
import sum25.group03.instrumentservice.audit.service.AuditLogService;
import sum25.group03.instrumentservice.audit.util.ObjectChangeDetector;

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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentServiceImpl implements InstrumentService {
    private final InstrumentRepository instrumentRepository;
    private final ConfigurationRepository configurationRepository;
    private final WarehouseServiceClient warehouseServiceClient;
    private final InstalledReagentRepository installedReagentRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final AuditLogService auditLogService;

    @Override
    @SkipAuditLog
    public ChangeInstrumentModeResponse changeInstrumentMode(ChangeInstrumentModeRequest request) {
        log.info("Starting change instrument mode process for instrument ID: {}", request.getInstrumentId());

        Instrument instrument = instrumentRepository.findById(request.getInstrumentId())
                .orElseThrow(() -> {
                    log.error("Instrument not found with ID: {}", request.getInstrumentId());
                    return new ResourceNotFoundException(
                            "Instrument not found with id: " + request.getInstrumentId());
                });

        log.info("Instrument found: {} ", instrument.getInstrumentName());

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

        List<AuditLog.FieldChange> changes = auditLogService.createFieldChanges(
                "status",
                previousStatus.toString(),
                request.getNewStatus().toString()
        );
        auditLogService.logWrite(
                "ChangeInstrumentMode",
                "Instrument",
                String.valueOf(updatedInstrument.getId()),
                "0.0.0.0",
                "Mozilla/5.0",
                changes
        );

        log.info("Instrument mode changed successfully from {} to {}", previousStatus, request.getNewStatus());

        if (request.getNewStatus() == InstrumentStatus.INACTIVE || request.getNewStatus() == InstrumentStatus.MAINTENANCE) {
            try {
                InstrumentModeChangedEvent event = InstrumentModeChangedEvent.builder()
                        .instrumentId(updatedInstrument.getId())
                        .instrumentName(updatedInstrument.getInstrumentName())
                        .previousStatus(String.valueOf(previousStatus))
                        .newStatus(String.valueOf(request.getNewStatus()))
                        .reason(request.getReason())
                        .changedDate(LocalDate.now())
                        .eventTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .build();

                kafkaEventPublisher.publishInstrumentModeChangedEvent(event);
                log.info("Instrument mode changed event published for instrument ID: {}", updatedInstrument.getId());
            } catch (Exception e) {
                log.error("Failed to publish instrument mode changed event, but mode change was successful: {}", e.getMessage());

            }
        }

        return ChangeInstrumentModeResponse.builder()
                .instrumentId(updatedInstrument.getId())
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
        log.info("Starting reagent installation process for instrument ID: {}", request.getInstrumentId());

        Instrument instrument = instrumentRepository.findById(request.getInstrumentId())
                .orElseThrow(() -> {
                    log.error("[v0] Instrument not found with ID: {}", request.getInstrumentId());
                    return new ResourceNotFoundException(
                            "Instrument not found with id: " + request.getInstrumentId());
                });

        log.info("Instrument found: {}", instrument.getInstrumentName());


        log.info("Validating reagent with Warehouse Service - batch number: {}", request.getLotNumber());
        ReagentValidationResponse reagentValidation;
        try {
            reagentValidation = warehouseServiceClient.validateReagent(request.getLotNumber(), request.getCurrentVolume());
        } catch (WarehouseServiceException e) {
            log.error("Warehouse Service validation failed: {}", e.getMessage());
            throw new WarehouseServiceException(
                    "Cannot install reagent: Unable to validate with Warehouse Service. " + e.getMessage());
        }


        if (!reagentValidation.isValid()) {
            log.warn("Reagent validation failed: {}", reagentValidation.getMessage());
            throw new InstrumentModeChangeException(
                    "Cannot install reagent: " + reagentValidation.getMessage());
        }
        if (!reagentValidation.isNotExpired()) {
            log.warn("Reagent validation failed: {}", reagentValidation.getMessage());
            throw new InstrumentModeChangeException(
                    "Cannot install reagent: " + reagentValidation.getMessage());
        }


        log.info("Reagent validation successful - reagent is valid and ready for use");

        if (request.getCurrentVolume() == null || request.getCurrentVolume() <= 0) {
            log.warn("Invalid current volume: {}", request.getCurrentVolume());
            throw new InstrumentModeChangeException("Current volume must be greater than 0");
        }

        List<InstalledReagent> reagents = installedReagentRepository
                .findByInstrumentIdAndStatusIsNot(request.getInstrumentId(), InstalledReagentStatus.REMOVED);
        for (InstalledReagent reagent : reagents) {
            if (reagent.getReagentId().equals(reagentValidation.getReagentId()) && reagent.getLotReagentId()!=null ) {
                log.warn("Reagent with ID {} is already installed on instrument ID {}",
                        reagent.getReagentId(), reagentValidation.getReagentId());
                throw new InstrumentModeChangeException(
                        "Reagent with ID " + reagent.getReagentId() +
                                " is already installed on this instrument. Please remove it before installing a new one.");
            }
        }


        InstalledReagent installedReagent = InstalledReagent.builder()
                .instrument(instrument)
                .currentVolume(request.getCurrentVolume())
                .status(InstalledReagentStatus.AVAILABLE)
                .unit(reagentValidation.getUnit())
                .installationDate(LocalDate.now())
                .lotReagentId(reagentValidation.getReagentId().intValue())
                .reagentId(reagentValidation.getReagentId())
                .reagentName(reagentValidation.getReagentName())
                .expirationDate(reagentValidation.getExpirationDate())
                .lotNumber(reagentValidation.getLotNumber())
                .build();

        InstalledReagent savedReagent = installedReagentRepository.save(installedReagent);
        try {

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String ipAddress = "unknown";
            String userAgent = "unknown";

            if (attributes != null) {
                HttpServletRequest httpRequest = attributes.getRequest();
                ipAddress = getClientIpAddress(httpRequest);
                userAgent = httpRequest.getHeader("User-Agent");
            }


            List<AuditLog.FieldChange> changes = new ArrayList<>();
            changes.add(
                    AuditLog.FieldChange.builder()
                            .field("id")
                            .oldValue(null)
                            .newValue(savedReagent.getId())
                            .build()
            );
            changes.add(
                    AuditLog.FieldChange.builder()
                            .field("status")
                            .oldValue(null)
                            .newValue(savedReagent.getStatus().toString())
                            .build()
            );
            changes.add(
                    AuditLog.FieldChange.builder()
                            .field("lotNumber")
                            .oldValue(null)
                            .newValue(savedReagent.getLotNumber())
                            .build()
            );

            auditLogService.logWrite(
                    "InstalledReagentServiceImpl.installReagent", // Tên hàm/Operation
                    "InstalledReagent",
                    savedReagent.getId().toString(),
                    ipAddress,
                    userAgent,
                    changes
            );
        } catch (Exception e) {
            // Quan trọng: Không để lỗi log ảnh hưởng tới nghiệp vụ chính
            log.warn("Failed to write audit log for installReagent: {}", e.getMessage());
        }

        log.info("Reagent installed successfully - ID: {}, Batch: {}",
                savedReagent.getId(), request.getLotNumber());

        try {
            ReagentInstalledEvent event = ReagentInstalledEvent.builder()
                    .reagentId(reagentValidation.getReagentId())
                    .reagentName(reagentValidation.getReagentName())
                    .lotNumber(request.getLotNumber())
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
                .reagentId(reagentValidation.getReagentId())
                .instrumentName(instrument.getInstrumentName())
                .reagentName(reagentValidation.getReagentName())
                .unit(reagentValidation.getUnit())
                .expirationDate(reagentValidation.getExpirationDate())
                .lotNumber(request.getLotNumber())
                .currentVolume(request.getCurrentVolume())
                .installationDate(savedReagent.getInstallationDate())
                .status(InstalledReagentStatus.AVAILABLE)
                .message("Reagent installed successfully and is available for operational use")
                .success(true)
                .build();
    }

    @Override
    public InstrumentResponse findInstrumentById(Long id) {
        log.info("Get instrument detail by id: {}", id);
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Instrument not found with ID: {}", id);
                    return new ResourceNotFoundException("Instrument not found with id: " + id);
                });
        return mapToResponse(instrument);
    }

    @Override
    public InstrumentPageResponse findAllInstruments(String keyword, String sort, String status, int page, int size) {
        log.info("Finding all instruments with keyword: {}, sort: {}, status: {}, page: {}, size: {}",
                keyword, sort, status, page, size);

        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("^(\\w+)(:)(asc|desc)$");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String column = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, column);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, column);
                }
            }
        }

        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));
        Page<Instrument> instrumentEntities;

        InstrumentStatus statusFilter = null;
        if (StringUtils.hasLength(status)) {
            try {
                statusFilter = InstrumentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
            }
        }

        if (StringUtils.hasLength(keyword) && statusFilter != null) {
            instrumentEntities = instrumentRepository.searchByKeywordsAndStatus(keyword, statusFilter, pageable);
        } else if (statusFilter != null) {
            instrumentEntities = instrumentRepository.findAllByStatus(statusFilter, pageable);
        } else if (StringUtils.hasLength(keyword)) {
            instrumentEntities = instrumentRepository.searchByKeywords(keyword, pageable);
        } else {
            instrumentEntities = instrumentRepository.findAllInstruments(pageable);
        }

        return getInstrumentPageResponse(page, size, instrumentEntities);
    }

    private InstrumentPageResponse getInstrumentPageResponse(int page, int size, Page<Instrument> instrumentEntities) {
        List<InstrumentResponse> instrumentList = instrumentEntities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        InstrumentPageResponse response = new InstrumentPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalPages(instrumentEntities.getTotalPages());
        response.setTotalElements(instrumentEntities.getTotalElements());
        response.setInstruments(instrumentList);
        return response;
    }

    private InstrumentResponse mapToResponse(Instrument instrument) {
        List<InstalledReagent> installedReagents = installedReagentRepository.findByInstrumentIdAndStatusIsNot(instrument.getId(), InstalledReagentStatus.REMOVED);

        List<sum25.group03.instrumentservice.controller.response.InstalledReagentResponse> reagentResponses =
                installedReagents.stream()
                        .map(reagent -> sum25.group03.instrumentservice.controller.response.InstalledReagentResponse.builder()
                                .id(reagent.getId())
                                .reagentId(reagent.getReagentId())
                                .reagentName(reagent.getReagentName())
                                .currentVolume(reagent.getCurrentVolume())
                                .status(reagent.getStatus())
                                .installationDate(reagent.getInstallationDate())
                                .lotReagentId(reagent.getLotReagentId())
                                .lotNumber(reagent.getLotNumber())
                                .unit(reagent.getUnit())
                                .build())
                        .collect(Collectors.toList());

        Configuration config = instrument.getConfiguration();
        return InstrumentResponse.builder()
                .id(instrument.getId())
                .instrumentName(instrument.getInstrumentName())
                .status(instrument.getStatus())
                .configurationId(config != null ? config.getId() : null)
                //.configurationName(config != null ? config.getConfigKey() : null)
                .installedReagents(reagentResponses)
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

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

}
