package sum25.group03.patientservice.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEntryMongoRepository extends MongoRepository<AuditEntryMongoRepository, String> {
}
