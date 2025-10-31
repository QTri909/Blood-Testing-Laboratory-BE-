package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.Reagents;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReagentRepo extends JpaRepository<Reagents, Long> {

    @Query("SELECT r.reagentId FROM Reagents r WHERE r.reagentId IN :reagentIds AND r.status = 'AVAILABLE'")
    List<Long> findExistingIds(List<Long> reagentIds);



    @Query("""
    SELECT r FROM Reagents r
    WHERE (:reagentName IS NULL OR :reagentName = '' OR LOWER(r.reagentName) LIKE LOWER(CONCAT('%', :reagentName, '%')))
""")
    Page<Reagents> filterReagents(@Param("reagentName") String reagentName, Pageable pageable);
}
