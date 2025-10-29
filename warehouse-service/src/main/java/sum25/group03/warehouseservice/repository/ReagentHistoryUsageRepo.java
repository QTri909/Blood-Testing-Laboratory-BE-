package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;

import java.util.List;

@Repository
public interface ReagentHistoryUsageRepo extends JpaRepository<ReagentHistoryUsage, Long> {
    List<ReagentHistoryUsage> findByReagent_ReagentNameContainingIgnoreCase(String reagentName);

    @EntityGraph(attributePaths = {"reagent", "instrument"})
    @Query("""
    SELECT u FROM ReagentHistoryUsage u
    JOIN u.reagent r
    WHERE
        (:keyword IS NULL OR CAST(:keyword AS string) = '' OR LOWER(r.reagentName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
    AND (:usageType IS NULL OR CAST(:usageType AS string) = '' OR LOWER(u.usageType) = LOWER(CAST(:usageType AS string)))
    AND (:instrumentId IS NULL OR u.instrument.instrumentId = :instrumentId)
    ORDER BY u.usedAt DESC
""")
    Page<ReagentHistoryUsage> searchUsageRecords(
            @Param("keyword") String keyword,
            @Param("usageType") String usageType,
            @Param("instrumentId") Long instrumentId,
            Pageable pageable);
}
