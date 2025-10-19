package sum25.group03.patientservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.entities.MedicalRecordEntity;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {
}