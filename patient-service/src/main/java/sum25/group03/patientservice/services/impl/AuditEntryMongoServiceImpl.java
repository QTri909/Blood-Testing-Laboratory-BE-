package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.common.response.events.MonitoringLogEvent;
import sum25.group03.patientservice.documents.AuditEntryDocument;
import sum25.group03.patientservice.dtos.response.AuditEntryResponseDTO;
import sum25.group03.patientservice.enums.ActionTypeFeatures;
import sum25.group03.patientservice.enums.DocumentType;
import sum25.group03.patientservice.mapper.AuditEntryMapper;
import sum25.group03.patientservice.repositories.mongo.AuditEntryMongoRepository;
import sum25.group03.patientservice.services.interfaces.AuditEntryMongoService;
import sum25.group03.patientservice.services.interfaces.IKafkaMonitoringLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEntryMongoServiceImpl implements AuditEntryMongoService {

    private final AuditEntryMongoRepository auditEntryMongoRepository;
    private final AuditEntryMapper auditEntryMapper;
    private final ActionLogService actionLogService;
    private final IKafkaMonitoringLog kafkaMonitoringLog;

    @Transactional
    public void saveAuditEntry(AuditEntryDocument auditEntryDocument) {
        auditEntryMongoRepository.save(auditEntryDocument);
    }

    // TODO: check permissions for viewerId to make sure only authorized users can view logs (ex: admin)
    // private boolean hasPermission(Long viewerId) {}

    public Page<AuditEntryResponseDTO> queryLogsWithPagination(Pageable pageable, DocumentType documentType, Long viewerId) {

        // log the action
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_AUDIT_LOGS, null);

        // query by document type
        Page<AuditEntryDocument> documentPage = auditEntryMongoRepository.findByEntityType(DocumentType.MEDICAL_RECORD, pageable);

        // map to dto list:
        List<AuditEntryResponseDTO> dtoList = documentPage.getContent().stream()
                .map(auditEntryMapper::toDto)
                .toList();

        try {
            // send log to monitoring service via kafka:
            MonitoringLogEvent logEvent = new MonitoringLogEvent(
                    ActionTypeFeatures.VIEW_AUDIT_LOGS.toString(),
                    "Viewer ID: " + viewerId,
                    "Queried audit logs of type: " + documentType,
                    "PatientService",
                    Map.of(
                            "totalLogs", documentPage.getTotalElements(),
                            "pageNumber", pageable.getPageNumber(),
                            "pageSize", pageable.getPageSize(),
                            "timestamp", LocalDateTime.now().toString()
                    )
            );
            kafkaMonitoringLog.sendMonitoringLog(logEvent);
        } catch (Exception e) {
            log.error("Failed to send monitoring log for viewing audit logs: {}", e.getMessage());
        }


        return new PageImpl<>(dtoList, documentPage.getPageable(), documentPage.getTotalElements());
    }
}
