package sum25.group03.patientservice.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.documents.AuditEntryDocument;

@Repository
public interface AuditEntryMongoRepository extends MongoRepository<AuditEntryDocument, String> {
}
