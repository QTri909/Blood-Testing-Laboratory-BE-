package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentHistorySupply;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistorySupplyRepo extends JpaRepository<ReagentHistorySupply, Long> {
    @Query("""
        SELECT rhs FROM ReagentHistorySupply rhs
        JOIN FETCH rhs.vendor v
        JOIN FETCH rhs.reagent r
        ORDER BY rhs.createdAt DESC
    """)
    Page<ReagentHistorySupply> findAllWithVendorAndReagent(Pageable pageable);
    @Query("""
    SELECT r.purchaseOrderNumber
    FROM ReagentHistorySupply r
    GROUP BY r.purchaseOrderNumber
    ORDER BY MAX(r.createdAt) DESC
""")
    Page<String> findDistinctPurchaseOrderNumbers(Pageable pageable);

    @Query("""
    SELECT DISTINCT r
    FROM ReagentHistorySupply r
    JOIN FETCH r.vendor v
    JOIN FETCH r.reagent re
    WHERE r.purchaseOrderNumber IN :poNumbers
""")
    List<ReagentHistorySupply> findAllByPurchaseOrderNumberInFetch(List<String> poNumbers);

    @EntityGraph(attributePaths = {"reagent"})
    List<ReagentHistorySupply> findAllByPurchaseOrderNumber(String purchaseOrderNumber);

    @EntityGraph(attributePaths = {"vendor", "reagent"})
    @Query("""
    SELECT s FROM ReagentHistorySupply s
    WHERE 
        (:vendorName IS NULL 
         OR :vendorName = '' 
         OR LOWER(s.vendor.vendorName) LIKE LOWER(CONCAT('%', :vendorName, '%')))
    AND 
        (:reagentName IS NULL 
         OR :reagentName = '' 
         OR LOWER(s.reagent.reagentName) LIKE LOWER(CONCAT('%', :reagentName, '%')))
    AND 
        (CAST(:startDate AS date) IS NULL OR s.receivedDate >= :startDate)
    AND 
        (CAST(:endDate AS date) IS NULL OR s.receivedDate <= :endDate)
""")
    Page<ReagentHistorySupply> filterSupplyHistory(
            @Param("vendorName") String vendorName,
            @Param("reagentName") String reagentName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}
