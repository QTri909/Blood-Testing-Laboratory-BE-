package sum25.group03.warehouseservice.audit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.audit.model.AuditLog;
import sum25.group03.warehouseservice.audit.provider.ActorProvider;
import sum25.group03.warehouseservice.audit.model.ActorContext;
import sum25.group03.warehouseservice.entity.Instrument;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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
    private static String lastHash = null;

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

    private void logActionWithStatus(String actionType, String operationName, String resourceType, String resourceId, String ipAddress, String userAgent, List<AuditLog.FieldChange> changes, AuditLog.Status status) {
        ActorContext actor = actorProvider.getCurrentActor();

        String previousHash = (lastHash != null) ? lastHash : "GENESIS";

        String dataToHash = previousHash
                + actionType
                + operationName
                + resourceType
                + resourceId
                + (actor.getUsername() != null ? actor.getUsername() : "system")
                + Instant.now().toString();

        String currentHash = calculateHash(dataToHash);

        AuditLog auditLog = AuditLog.builder()
                .timestamp(Instant.now())
                .actor(AuditLog.Actor.builder()
                        .type(actor.getType())
                        .id(actor.getId())
                        .username(actor.getUsername())
                        .principal(actor.getPrincipal())
                        .build())
                .source(AuditLog.Source.builder()
                        .service("warehouse-service")
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
                .previousHash(previousHash)
                .currentHash(currentHash)
                .build();

        cloudWatchAuditLogger.log(auditLog);
        lastHash = currentHash;
    }

    public void logWrite(String activateInstrument, String instrument, String string, String system, String s, String s1, Instrument instrument1) {
    }

    private String calculateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : encoded) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error generating hash", e);
            return null;
        }
    }
}

