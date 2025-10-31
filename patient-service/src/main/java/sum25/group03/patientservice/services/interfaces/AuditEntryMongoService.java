package sum25.group03.patientservice.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.patientservice.documents.AuditEntryDocument;
import sum25.group03.patientservice.dtos.response.AuditEntryResponseDTO;
import sum25.group03.patientservice.enums.DocumentType;

public interface AuditEntryMongoService {
    void saveAuditEntry(AuditEntryDocument auditEntryDocument);
    Page<AuditEntryResponseDTO> queryLogsWithPagination(Pageable pageable, DocumentType documentType, Long viewerId);
}
