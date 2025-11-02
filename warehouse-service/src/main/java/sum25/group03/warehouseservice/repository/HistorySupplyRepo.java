package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentHistorySupply;
import sum25.group03.warehouseservice.entity.Reagents;

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
}
