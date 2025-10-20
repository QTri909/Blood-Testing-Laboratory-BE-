package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;

import java.util.List;

@Repository
public interface ReagentUsageRepo extends JpaRepository<ReagentHistoryUsage, Long> {

    @Query("""
        SELECT ru.reagent.reagentId
        FROM ReagentHistoryUsage ru
        JOIN Instrument i ON ru.instrument.instrumentId = i.instrumentId
        WHERE ru.instrument.instrumentId = :id
   """)
    List<Long> findIdsByInstrumentId(@Param("id") Long instrumentId);
}
