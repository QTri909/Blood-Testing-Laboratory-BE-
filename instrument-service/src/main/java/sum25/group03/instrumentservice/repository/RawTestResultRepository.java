package sum25.group03.instrumentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.model.RawTestResult;

import java.util.List;

@Repository
public interface RawTestResultRepository extends JpaRepository<RawTestResult, Integer> {
    List<RawTestResult> findByTestOrderId(Integer testOrderId);
    List<RawTestResult> findByInstrumentId(Integer instrumentId);
    List<RawTestResult> findByIsSyncedFalse();
    List<RawTestResult> findByIsSentToMonitoringFalse();
}

