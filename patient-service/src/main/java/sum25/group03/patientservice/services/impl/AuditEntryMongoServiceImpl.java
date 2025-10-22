package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.documents.AuditEntryDocument;
import sum25.group03.patientservice.repositories.mongo.AuditEntryMongoRepository;
import sum25.group03.patientservice.services.interfaces.AuditEntryMongoService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEntryMongoServiceImpl implements AuditEntryMongoService {

    private final AuditEntryMongoRepository auditEntryMongoRepository;

    @Transactional
    public void saveAuditEntry(AuditEntryDocument auditEntryDocument) {
        auditEntryMongoRepository.save(auditEntryDocument);
        log.info("Audit entry saved successfully to mongoDb!");
        log.info("Saved document: {}", auditEntryDocument);
    }
}
