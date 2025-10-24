package sum25.group03.patientservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.entities.ClinicalNoteEntity;

import java.util.List;

@Repository("postgresClinicalNoteRepository")
public interface ClinicalNoteRepository extends JpaRepository<ClinicalNoteEntity, Long> {
    List<ClinicalNoteEntity> findByRecordId(Long recordId);
}
