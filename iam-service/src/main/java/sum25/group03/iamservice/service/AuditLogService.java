package sum25.group03.iamservice.service;

public interface AuditLogService {
    void record(String action, String entityName, Long entityId, String performedBy, String details);
}
