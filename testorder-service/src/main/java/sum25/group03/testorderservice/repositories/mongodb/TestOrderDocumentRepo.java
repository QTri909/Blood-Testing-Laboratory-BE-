package sum25.group03.testorderservice.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entities.mongodb.TestOrderDocument;

import java.util.Optional;

@Repository
public interface TestOrderDocumentRepo extends MongoRepository<TestOrderDocument, Long> {
    // search by testOrderId
    Optional<TestOrderDocument> findByTestOrderId(Long testOrderId);
}
