package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import java.util.List;
import java.util.Optional;

import sum25.group03.testorderservice.entities.TestOrder;


@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder,Long>, JpaSpecificationExecutor<TestOrder> {
    List<TestOrder> findByPatientId(Long patientId);
    List<TestOrder> findByStatus(TestOrderStatus status);
    List<TestOrder> findByCreatedBy(Long createdBy);

//    @EntityGraph(attributePaths = {"testResults.parameter", "comments"})
//    Optional<TestOrder> findById(Long id);

    Optional<TestOrder> findFirstByBarcodeOrderByCreatedAtDesc(String barcode);
    Optional<TestOrder> findTopByPatientIdOrderByCreatedAtDesc(Long patientId);
}
