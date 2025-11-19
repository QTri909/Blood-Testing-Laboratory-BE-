package sum25.group03.iamservice.service.Interface;

import sum25.group03.iamservice.dto.response.AuditLogReponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface AuditLogService {
    void record(String action, String entityName, Long entityId, String performedBy, String details);
    List<AuditLogReponse> getAuditLogs(String entityName, Long entityId);
    ByteArrayInputStream exportLogsAsExcel(String entityName, Long entityId) throws IOException;
}
