package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentHistorySupply;
import sum25.group03.warehouseservice.entity.Reagents;

@Repository
public interface HistorySupplyRepo extends JpaRepository<ReagentHistorySupply, Long> {
    @Query("""
        SELECT rhs FROM ReagentHistorySupply rhs
        JOIN FETCH rhs.vendor v
        JOIN FETCH rhs.reagent r
        ORDER BY rhs.createdAt DESC
    """)
    Page<ReagentHistorySupply> findAllWithVendorAndReagent(Pageable pageable);
}
