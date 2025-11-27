package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.enums.ReagentInventoryStatus;

import java.time.LocalDate;
import java.util.List;
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

    @Query("""
        SELECT COALESCE(SUM(ri.quantityAvailable), 0)
        FROM ReagentInventory ri
        WHERE ri.reagent.reagentId = :reagentId
    """)
    Double getTotalQuantitysByReagentId(@Param("reagentId") Long reagentId);

    // Count reagents where the total available across all lots is strictly less than the reagent's min_stock_level
    // Use native SQL without DB-specific casting and return Long
    @Query(value = "SELECT COUNT(*) FROM reagents r WHERE COALESCE((SELECT SUM(ri.quantity_available) FROM reagent_inventory ri WHERE ri.reagent_id = r.reagent_id),0) < COALESCE(r.min_stock_level, 0)", nativeQuery = true)
    Long countLowStockReagents();

    // Use JPQL count for expiring lots which returns Long
    @Query("SELECT COUNT(ri) FROM ReagentInventory ri WHERE ri.expiryDate <= :dateLimit")
    Long countExpiringLots(@Param("dateLimit") LocalDate dateLimit);

    // Batch fetch inventories for given reagent ids
    @Query("SELECT ri FROM ReagentInventory ri WHERE ri.reagent.reagentId IN :ids")
    List<ReagentInventory> findAllByReagentIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT ri FROM ReagentInventory ri WHERE ri.reagent.reagentId = :reagentId AND COALESCE(ri.quantityAvailable, 0) > 0 AND ri.status = :status ORDER BY ri.expiryDate ASC ")
    List<ReagentInventory> findAllByReagentId(@Param("reagentId") Long reagentId, @Param("status")ReagentInventoryStatus status);
}
