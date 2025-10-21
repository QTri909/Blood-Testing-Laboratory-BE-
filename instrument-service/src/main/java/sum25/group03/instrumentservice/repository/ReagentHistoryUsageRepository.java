package sum25.group03.instrumentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.model.ReagentHistoryUsage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReagentHistoryUsageRepository extends JpaRepository<ReagentHistoryUsage, Integer> {
    List<ReagentHistoryUsage> findByReagentId(Integer reagentId);
    List<ReagentHistoryUsage> findByInstrumentId(Integer instrumentId);
    List<ReagentHistoryUsage> findByUsedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
