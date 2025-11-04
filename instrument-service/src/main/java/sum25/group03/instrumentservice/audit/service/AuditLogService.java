package sum25.group03.instrumentservice.audit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.audit.model.AuditLog;
import sum25.group03.instrumentservice.audit.model.ActorContext;
import sum25.group03.instrumentservice.audit.provider.ActorProvider;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final CloudWatchAuditLogger cloudWatchAuditLogger;
    private final ActorProvider actorProvider;

    public void logRead(String operationName, String resourceType, String resourceId,
                        String ipAddress, String userAgent) {
        logAction("READ", operationName, resourceType, resourceId, ipAddress, userAgent, null, null);
    }

    public void logWrite(String operationName, String resourceType, String resourceId,
                         String ipAddress, String userAgent, List<AuditLog.FieldChange> changes) {
        logAction("WRITE", operationName, resourceType, resourceId, ipAddress, userAgent, changes, null);
    }

    public void logWriteSuccess(String operationName, String resourceType, String resourceId,
                                String ipAddress, String userAgent, List<AuditLog.FieldChange> changes) {
        logAction("WRITE", operationName, resourceType, resourceId, ipAddress, userAgent, changes, "SUCCESS");
    }

    public void logWriteFailure(String operationName, String resourceType, String resourceId,
                                String ipAddress, String userAgent, String errorCode, String errorMessage) {
        AuditLog.Status status = AuditLog.Status.builder()
                .outcome("FAILURE")
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();

        logActionWithStatus("WRITE", operationName, resourceType, resourceId, ipAddress, userAgent, null, status);
    }

    public void logSystemEvent(String operationName, String resourceType, String resourceId,
                               List<AuditLog.FieldChange> changes, String outcome) {
        AuditLog.Status status = AuditLog.Status.builder()
                .outcome(outcome != null ? outcome : "SUCCESS")
                .build();

        logSystemActionWithStatus("WRITE", operationName, resourceType, resourceId, changes, status);
    }

    public List<AuditLog.FieldChange> createFieldChanges(String fieldName, Object oldValue, Object newValue) {
        List<AuditLog.FieldChange> changes = new ArrayList<>();
        String oldValueStr = oldValue != null ? oldValue.toString() : "null";
        String newValueStr = newValue != null ? newValue.toString() : "null";
        changes.add(new AuditLog.FieldChange(fieldName, oldValueStr, newValueStr));
        return changes;
    }

    private void logAction(String actionType, String operationName, String resourceType, String resourceId,
                           String ipAddress, String userAgent, List<AuditLog.FieldChange> changes, String outcome) {
        AuditLog.Status status = AuditLog.Status.builder()
                .outcome(outcome != null ? outcome : "SUCCESS")
                .build();

        logActionWithStatus(actionType, operationName, resourceType, resourceId, ipAddress, userAgent, changes, status);
    }

    private void logActionWithStatus(String actionType, String operationName, String resourceType, String resourceId,
                                     String ipAddress, String userAgent, List<AuditLog.FieldChange> changes,
                                     AuditLog.Status status) {
        ActorContext actor = actorProvider.getCurrentActor();

        AuditLog auditLog = AuditLog.builder()
                .timestamp(Instant.now())
                .actor(AuditLog.Actor.builder()
                        .type(actor.getType())
                        .id(actor.getId())
                        .username(actor.getUsername())
                        .principal(actor.getPrincipal())
                        .build())
                .source(AuditLog.Source.builder()
                        .service("instrument-service")
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .correlationId("cor-" + UUID.randomUUID())
                        .build())
                .action(AuditLog.Action.builder()
                        .type(actionType)
                        .operation(operationName)
                        .name(operationName)
                        .build())
                .resource(AuditLog.Resource.builder()
                        .type(resourceType)
                        .id(resourceId)
                        .build())
                .status(status)
                .changes(changes)
                .build();

        cloudWatchAuditLogger.log(auditLog);
    }

    private void logSystemActionWithStatus(String actionType, String operationName, String resourceType, String resourceId,
                                           List<AuditLog.FieldChange> changes, AuditLog.Status status) {
        ActorContext actor = actorProvider.getCurrentActor();

        AuditLog auditLog = AuditLog.builder()
                .timestamp(Instant.now())
                .actor(AuditLog.Actor.builder()
                        .type("SYSTEM")
                        .id("system")
                        .username("system")
                        .principal("auto-delete-job")
                        .build())
                .source(AuditLog.Source.builder()
                        .service("instrument-service")
                        .ipAddress("system")
                        .userAgent("auto-delete-job")
                        .correlationId("cor-" + UUID.randomUUID())
                        .build())
                .action(AuditLog.Action.builder()
                        .type(actionType)
                        .operation(operationName)
                        .name(operationName)
                        .build())
                .resource(AuditLog.Resource.builder()
                        .type(resourceType)
                        .id(resourceId)
                        .build())
                .status(status)
                .changes(changes)
                .build();

        cloudWatchAuditLogger.logSystemEvent(auditLog);
    }
}
