package sum25.group03.patientservice.repositories.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.entities.elasticsearch.MedicalRecord;

@Repository
public interface MedicalRecordElasticSearchRepository extends ElasticsearchRepository<MedicalRecord, Long> {
    MedicalRecord findByPatientId(Long patientId);
    MedicalRecord findByAssignedUser(Long assignedUser);
}
