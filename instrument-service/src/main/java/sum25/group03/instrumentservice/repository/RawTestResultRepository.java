package sum25.group03.instrumentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.model.RawTestResult;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RawTestResultRepository extends JpaRepository<RawTestResult, Long> {
    List<RawTestResult> findByTestOrderId(Long testOrderId);
    List<RawTestResult> findByInstrumentId(Long instrumentId);
    List<RawTestResult> findByIsSyncedFalse();
    List<RawTestResult> findByIsSentToMonitoringFalse();

    @Query("SELECT r FROM RawTestResult r WHERE " +
            "(r.isSentToMonitoring = true OR r.isSynced = true) AND " +
            "r.createdAt < :cutoffDate")
    List<RawTestResult> findOldBackedUpResults(@Param("cutoffDate") LocalDateTime cutoffDate);
}
