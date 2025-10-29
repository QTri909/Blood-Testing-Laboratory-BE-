package sum25.group03.patientservice.repositories.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.entities.elasticsearch.ESMedicalRecord;

@Repository
public interface MedicalRecordESRepository extends ElasticsearchRepository<ESMedicalRecord, Long> {
    ESMedicalRecord findByPatientId(Long patientId);
    ESMedicalRecord findByAssignedUser(Long assignedUser);
}
