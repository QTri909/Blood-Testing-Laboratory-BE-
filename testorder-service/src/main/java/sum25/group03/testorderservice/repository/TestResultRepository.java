package sum25.group03.testorderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entity.TestResult;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult,Long> {
    List<TestResult> findByTestOrderId(Long testOrderId);

    List<TestResult> findByInstrumentId(Long instrumentId);

    List<TestResult> findByParameterId(Long parameterId);
}
