package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.entities.TestResult;
import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult,Long> {
    List<TestResult> findByTestOrderId(Long testOrderId);
    List<TestResult> findByParameterId(Long parameterId);
    List<TestResult> findByTestOrderOrderByCreatedAtDesc(TestOrder testOrder);

    List<TestResult> findByIdInOrderByCreatedAtDesc(List<Long> ids);
    List<TestResult> findByTestOrderAndParameter_IdInOrderByCreatedAtDesc(TestOrder testOrder, List<Long> parameterIds);
}
