package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.testorderservice.entities.TestResult;

public interface TestResultRepository extends JpaRepository<TestResult,Long> {
}
