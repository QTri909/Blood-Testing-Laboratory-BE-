package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;

import java.util.List;

@Repository
public interface ReagentRepo extends JpaRepository<Reagents, Long> {

    @Query("SELECT r.reagentId FROM Reagents r WHERE r.reagentId IN :reagentIds AND r.status = 'AVAILABLE'")
    List<Long> findExistingIds(List<Long> reagentIds);

    @Query("""
    SELECT DISTINCT r
    FROM Reagents r
    JOIN r.reagentHistoryUsages rhu
    JOIN rhu.instrument i
    WHERE i.instrumentId = :instrumentId
      AND r.status = 'ACTIVE'
""")
    List<Reagents> findAllByInstrumentId(@Param("instrumentId") Long instrumentId);

    @Query("""
        SELECT r
        FROM Reagents r
        WHERE r.reagentId IN :reagentId AND r.status = 'ACTIVE'
     """)
    List<Reagents> findAllByReagentId(@Param("reagentId") List<Long> reagentId);

    @Query("""
    SELECT r FROM Reagents r
    WHERE (:reagentName IS NULL OR :reagentName = '' OR LOWER(r.reagentName) LIKE LOWER(CONCAT('%', :reagentName, '%')))
""")
    Page<Reagents> filterReagents(@Param("reagentName") String reagentName, Pageable pageable);

    List<Reagents> findAllByStatus(ReagentStatus status);

    @Query("SELECT DISTINCT r FROM Reagents r")
    List<Reagents> findAllDistinct();

    // Support pageable return type so service can request a Page
    Page<Reagents> findAllByStatus(ReagentStatus reagentStatus, Pageable pageable);

    boolean existsByCatalogNumber(String catalogNumber);

    boolean existsByReagentName(String reagentName);
}
