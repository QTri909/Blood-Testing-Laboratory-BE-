package sum25.group03.iamservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.iamservice.entity.AuditLog;
import sum25.group03.iamservice.repository.AuditLogRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void record(String action, String entityName, Long entityId, String performedBy, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}
