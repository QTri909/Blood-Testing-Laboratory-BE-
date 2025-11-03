package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.Reagents;

import java.util.Optional;

@Repository
public interface ReagentInventoryRepo extends JpaRepository<ReagentInventory, Long> {

    @Query("SELECT ri FROM ReagentInventory ri JOIN FETCH ri.reagent r WHERE ri.lotNumber = :lotNumber")
    Optional<ReagentInventory> findByLotNumber(@Param("lotNumber") String lotNumber);

    @Query("""
        SELECT COALESCE(SUM(ri.quantityAvailable), 0)
        FROM ReagentInventory ri
        WHERE ri.reagent.reagentId = :reagentId
    """)
    Integer getTotalQuantityByReagentId(@Param("reagentId") Long reagentId);

    Optional<ReagentInventory> findByReagentIdAndLotNumber(Long reagentId, String lotNumber);
}
