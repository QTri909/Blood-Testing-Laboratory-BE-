package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;

import java.util.List;

@Repository
public interface ReagentUsageRepo extends JpaRepository<ReagentHistoryUsage, Long> {

    List<Long> findAllByInstrument_InstrumentId(long instrumentId);
}
