package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.entity.Reagents;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReagentUsageRepo extends JpaRepository<ReagentHistoryUsage, Long> {


    List<Long> findAllByInstrument_InstrumentId(long instrumentId);
    @EntityGraph(attributePaths = {"reagent"})
    @Query("SELECT rhu FROM ReagentHistoryUsage rhu WHERE rhu.instrument.instrumentId = :id")
    List<ReagentHistoryUsage> findAllByInstrument(@Param("id") Long instrumentId);
    List<ReagentHistoryUsage> findTop3ByReagentOrderByUsedAtDesc(Reagents reagent);
}
