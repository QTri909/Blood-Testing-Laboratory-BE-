package sum25.group03.patientservice.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.entities.UserSnapshotEntity;

import java.util.Optional;

@Repository
public interface UserSnapshotRepository extends JpaRepository<UserSnapshotEntity, Long> {
    Optional<UserSnapshotEntity> findByExternalUserId(Long externalUserId);
    boolean existsByExternalUserId(Long externalUserId);
}
