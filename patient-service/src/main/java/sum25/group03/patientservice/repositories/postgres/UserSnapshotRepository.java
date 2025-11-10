package sum25.group03.patientservice.repositories.postgres;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.patientservice.entities.UserSnapshotEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSnapshotRepository extends JpaRepository<UserSnapshotEntity, Long> {
    Optional<UserSnapshotEntity> findByExternalUserId(Long externalUserId);
    boolean existsByExternalUserId(Long externalUserId);

    // find all in role list if 'PATIENT' is included, with pagination
    @Query(
            value = "SELECT * FROM user_snapshot p WHERE p.roles::jsonb @> cast(:role AS jsonb)",
            countQuery = "SELECT count(*) FROM user_snapshot p WHERE p.roles::jsonb @> cast(:role AS jsonb)",
            nativeQuery = true
    )
    Page<UserSnapshotEntity> findByRolesContaining(@Param("role") String role, Pageable pageable);

    Collection<UserSnapshotEntity> findByExternalUserIdIn(List<Long> patientIds);
}
