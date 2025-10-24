package sum25.group03.patientservice.repositories.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.documents.AuditEntryDocument;
import sum25.group03.patientservice.enums.DocumentType;


@Repository
public interface AuditEntryMongoRepository extends MongoRepository<AuditEntryDocument, String> {
    Page<AuditEntryDocument> findByEntityType(DocumentType entityType, Pageable pageable);
}