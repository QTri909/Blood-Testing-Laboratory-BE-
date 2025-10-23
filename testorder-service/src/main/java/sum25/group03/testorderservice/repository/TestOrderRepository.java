package sum25.group03.testorderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entity.TestOrder;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.util.List;

@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder,Long> {
    List<TestOrder> findByPatientId(Long patientId);

    List<TestOrder> findByStatus(TestOrderStatus status);

    List<TestOrder> findByCreatedBy(Long createdBy);
}
