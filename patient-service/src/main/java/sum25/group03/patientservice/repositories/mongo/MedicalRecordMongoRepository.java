package sum25.group03.patientservice.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.documents.MedicalRecordDocument;
import sum25.group03.patientservice.entities.MedicalRecordEntity;

import java.util.Optional;

@Repository
public interface MedicalRecordMongoRepository extends MongoRepository<MedicalRecordDocument, String> {

    Optional<MedicalRecordDocument> findMedicalRecordDocumentByRecordId(Long recordId);
    Optional<MedicalRecordDocument> findMedicalRecordDocumentByRecordCode(String recordCode);
}