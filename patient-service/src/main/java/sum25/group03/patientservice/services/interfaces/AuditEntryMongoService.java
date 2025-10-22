package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.documents.AuditEntryDocument;

public interface AuditEntryMongoService {
    void saveAuditEntry(AuditEntryDocument auditEntryDocument);
}
