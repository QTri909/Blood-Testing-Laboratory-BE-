package sum25.group03.instrumentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.model.ReagentHistoryUsage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReagentHistoryUsageRepository extends JpaRepository<ReagentHistoryUsage, Integer> {
    List<ReagentHistoryUsage> findByLotReagentId(Integer reagentBatchId);
    List<ReagentHistoryUsage> findByInstrumentId(Integer instrumentId);
    List<ReagentHistoryUsage> findByUsedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT rhu FROM ReagentHistoryUsage rhu WHERE rhu.instrument.id = :instrumentId AND COALESCE(rhu.volumeUsed, 0) > 0 ORDER BY rhu.usedAt DESC")
    Page<ReagentHistoryUsage> findAllByInstrument_InstrumentId(Long instrumentId, Pageable pageable);
}

