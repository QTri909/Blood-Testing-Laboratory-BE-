package sum25.group03.instrumentservice.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sum25.group03.instrumentservice.audit.annotation.SkipAuditLog;
import sum25.group03.instrumentservice.audit.model.AuditLog;
import sum25.group03.instrumentservice.audit.service.AuditLogService;
import sum25.group03.instrumentservice.audit.util.ObjectChangeDetector;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.controller.request.UpdateReagentStatusRequest;
import sum25.group03.instrumentservice.controller.response.InstalledReagentDetailResponse;
import sum25.group03.instrumentservice.controller.response.InstalledReagentPageResponse;
import sum25.group03.instrumentservice.controller.response.InstalledReagentResponse;
import sum25.group03.instrumentservice.controller.response.UpdateReagentStatusResponse;
import sum25.group03.instrumentservice.exception.InstrumentModeChangeException;
import sum25.group03.instrumentservice.exception.ResourceNotFoundException;
import sum25.group03.instrumentservice.model.InstalledReagent;
import sum25.group03.instrumentservice.repository.InstalledReagentRepository;
import sum25.group03.instrumentservice.service.InstalledReagentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstalledReagentServiceImpl implements InstalledReagentService {

    private final InstalledReagentRepository installedReagentRepository;
    private final AuditLogService auditLogService;

    @Override
    public InstalledReagentPageResponse findInstalledReagentById(Long id) {
        log.info("Get installed reagent detail by id: {}", id);
        InstalledReagent reagent = installedReagentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Installed reagent not found with ID: {}", id);
                    return new ResourceNotFoundException("Installed reagent not found with id: " + id);
                });

        InstalledReagentDetailResponse reagentDetail = mapToInstalledReagentDetailResponse(reagent);

        InstalledReagentPageResponse response = new InstalledReagentPageResponse();
        response.setPageNumber(1);
        response.setPageSize(1);
        response.setTotalPages(1);
        response.setTotalElements(1);
        response.setReagents(List.of(reagentDetail));
        return response;
    }

    @Override
    public InstalledReagentPageResponse findAllInstalledReagents(String keyword, String sort, String status,
                                                                 Integer instrumentId, int page, int size) {
        log.info("Finding all installed reagents with keyword: {}, sort: {}, status: {}, instrumentId: {}, page: {}, size: {}",
                keyword, sort, status, instrumentId, page, size);

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
        Page<InstalledReagent> reagentEntities;

        InstalledReagentStatus statusFilter = null;
        if (StringUtils.hasLength(status)) {
            try {
                statusFilter = InstalledReagentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
            }
        }


        if (StringUtils.hasLength(keyword) && statusFilter != null && instrumentId != null) {
            reagentEntities = installedReagentRepository.searchByKeywordsStatusAndInstrumentId(keyword, statusFilter, instrumentId, pageable);
        } else if (StringUtils.hasLength(keyword) && statusFilter != null) {
            reagentEntities = installedReagentRepository.searchByKeywordsAndStatus(keyword, statusFilter, pageable);
        } else if (StringUtils.hasLength(keyword) && instrumentId != null) {
            reagentEntities = installedReagentRepository.searchByKeywordsAndInstrumentId(keyword, instrumentId, pageable);
        } else if (statusFilter != null && instrumentId != null) {
            reagentEntities = installedReagentRepository.findByInstrumentIdAndStatus(instrumentId, statusFilter, pageable);
        } else if (StringUtils.hasLength(keyword)) {
            reagentEntities = installedReagentRepository.searchByKeywords(keyword, pageable);
        } else if (statusFilter != null) {
            reagentEntities = installedReagentRepository.findByStatusPaged(statusFilter, pageable);
        } else if (instrumentId != null) {
            reagentEntities = installedReagentRepository.findByInstrumentIdPaged(instrumentId, pageable);
        } else {
            reagentEntities = installedReagentRepository.findAllReagents(pageable);
        }

        return getInstalledReagentPageResponse(page, size, reagentEntities);
    }

    private InstalledReagentPageResponse getInstalledReagentPageResponse(int page, int size, Page<InstalledReagent> reagentEntities) {
        List<InstalledReagentDetailResponse> reagentList = reagentEntities.stream()
                .map(this::mapToInstalledReagentDetailResponse)
                .collect(Collectors.toList());

        InstalledReagentPageResponse response = new InstalledReagentPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalPages(reagentEntities.getTotalPages());
        response.setTotalElements(reagentEntities.getTotalElements());
        response.setReagents(reagentList);
        return response;
    }

    private InstalledReagentDetailResponse mapToInstalledReagentDetailResponse(InstalledReagent reagent) {
        return InstalledReagentDetailResponse.builder()
                .id(reagent.getId())
                .instrumentId(reagent.getInstrument().getId())
                .instrumentName(reagent.getInstrument().getInstrumentName())
                .lotReagentId(reagent.getLotReagentId())
                .currentVolume(reagent.getCurrentVolume())
                .status(reagent.getStatus())
                .installationDate(reagent.getInstallationDate())
                .expirationDate(reagent.getExpirationDate())
                .reagentId(reagent.getReagentId())
                .reagentName(reagent.getReagentName())
                .lotNumber(reagent.getLotNumber())
                .unit(reagent.getUnit())
                .build();
    }

    @Override
    @SkipAuditLog
    public UpdateReagentStatusResponse updateReagentStatus(UpdateReagentStatusRequest request) {
        log.info("Starting reagent status update process for installed reagent ID: {}", request.getInstalledReagentId());

        InstalledReagent installedReagent = installedReagentRepository.findByReagentIdAndInstrumentIdAndStatusNot(request.getInstalledReagentId(),request.getInstrumentId(), InstalledReagentStatus.REMOVED)
                .orElseThrow(() -> {
                    log.error("Installed reagent not found with ID: {}", request.getInstalledReagentId());
                    return new ResourceNotFoundException(
                            "Installed reagent not found with id: " + request.getInstalledReagentId());
                });

        log.info("Installed reagent found - current status: {}", installedReagent.getStatus());


        if (installedReagent.getStatus() == request.getNewStatus()) {
            log.warn("Attempted to update reagent to same status: {}", request.getNewStatus());
            throw new InstrumentModeChangeException(
                    "Reagent is already in " + request.getNewStatus() + " status. No change needed.");
        }

        validateStatusTransition(installedReagent.getStatus(), request.getNewStatus());

        InstalledReagentStatus previousStatus = installedReagent.getStatus();
        installedReagent.setStatus(request.getNewStatus());
        InstalledReagent updatedReagent = installedReagentRepository.save(installedReagent);

        List<AuditLog.FieldChange> changes = auditLogService.createFieldChanges(
                "status",
                previousStatus.toString(),
                request.getNewStatus().toString()
        );
        auditLogService.logWrite(
                "UpdateReagentStatus",
                "InstalledReagent",
                String.valueOf(updatedReagent.getId()),
                "0.0.0.0",
                "Mozilla/5.0",
                changes
        );

        log.info("Reagent status updated successfully from {} to {} by user: {}",
                previousStatus, request.getNewStatus(), request.getChangedBy());

        return UpdateReagentStatusResponse.builder()
                .installedReagentId(updatedReagent.getId())
                .instrumentId(updatedReagent.getInstrument().getId())
                .instrumentName(updatedReagent.getInstrument().getInstrumentName())
                .previousStatus(previousStatus)
                .newStatus(request.getNewStatus())
                .changedAt(LocalDateTime.now())
                .changedBy(request.getChangedBy())
                .reason(request.getReason())
                .message("Reagent status updated successfully from " + previousStatus + " to " + request.getNewStatus())
                .success(true)
                .build();
    }

    @Override
    public Map<Long, String> getAllReagentByInstrumentId(Long instrumentId) {
        List<InstalledReagent> reagents = installedReagentRepository.findByInstrumentIdAndStatusIsNot(
                instrumentId,
                InstalledReagentStatus.REMOVED
        );

        // map id reagent with its name
        Map<Long, String> reagentIdNameMap = reagents.stream()
                .collect(Collectors.toMap(InstalledReagent::getReagentId, InstalledReagent::getReagentName, (name1, name2) -> name1));

        return reagentIdNameMap;
    }

    @Override
    public void deleteReagents(Long reagentId) {
        InstalledReagent reagent = installedReagentRepository.findById(reagentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installed reagent not found with id: " + reagentId));
        reagent.setStatus(InstalledReagentStatus.REMOVED);
        installedReagentRepository.save(reagent);
        log.info("Installed reagent with id: {} has been marked as REMOVED", reagentId);
    }

    private void validateStatusTransition(InstalledReagentStatus currentStatus, InstalledReagentStatus newStatus) {
        log.info("Validating status transition from {} to {}", currentStatus, newStatus);

        boolean isValidTransition = false;

        switch (currentStatus) {
            case AVAILABLE:
                isValidTransition = newStatus == InstalledReagentStatus.IN_USE ||
                        newStatus == InstalledReagentStatus.QUARANTINED ||
                        newStatus == InstalledReagentStatus.REMOVED ||
                        newStatus == InstalledReagentStatus.EXPIRED||
                        newStatus == InstalledReagentStatus.EMPTY;
                break;
            case IN_USE:
                isValidTransition = newStatus == InstalledReagentStatus.LOW_VOLUME ||
                        newStatus == InstalledReagentStatus.QUARANTINED ||
                        newStatus == InstalledReagentStatus.AVAILABLE ||
                        newStatus == InstalledReagentStatus.EXPIRED;
                break;
            case LOW_VOLUME:
                isValidTransition = newStatus == InstalledReagentStatus.EMPTY ||
                        newStatus == InstalledReagentStatus.QUARANTINED ||
                        newStatus == InstalledReagentStatus.REMOVED ||
                        newStatus == InstalledReagentStatus.EXPIRED;
                break;
            case EMPTY:
                isValidTransition = newStatus == InstalledReagentStatus.REMOVED;
                break;
            case EXPIRED:
                isValidTransition = newStatus == InstalledReagentStatus.REMOVED;
                break;
            case QUARANTINED:
                isValidTransition = newStatus == InstalledReagentStatus.AVAILABLE ||
                        newStatus == InstalledReagentStatus.REMOVED;
                break;
            case REMOVED:
                isValidTransition = false;
                break;
        }

        if (!isValidTransition) {
            log.warn("Invalid status transition from {} to {}", currentStatus, newStatus);
            throw new InstrumentModeChangeException(
                    "Invalid status transition: Cannot change from " + currentStatus + " to " + newStatus);
        }

        log.info("Status transition validation passed");
    }


}
