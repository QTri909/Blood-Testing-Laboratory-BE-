package sum25.group03.patientservice.repositories.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.entities.ClinicalNoteEntity;

@Repository
public interface ClinicalNoteRepository extends JpaRepository<ClinicalNoteEntity, Long> {
}
