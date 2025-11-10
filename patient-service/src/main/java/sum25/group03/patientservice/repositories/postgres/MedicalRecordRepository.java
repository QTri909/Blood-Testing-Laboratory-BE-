package sum25.group03.patientservice.repositories.postgres;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.entities.MedicalRecordEntity;
import sum25.group03.patientservice.enums.MedicalRecordStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {
    Optional<MedicalRecordEntity> findByRecordCode(UUID recordCode);
    List<MedicalRecordEntity> findByPatientId(Long patientId);
    Optional<MedicalRecordEntity> findTopByPatientIdOrderByVisitDateDesc(Long patientId);

    // find by id and status is not DELETED
    Optional<MedicalRecordEntity> findByRecordIdAndStatusNot(Long recordId, MedicalRecordStatus status);

}