package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import sum25.group03.testorderservice.entities.TestOrder;


@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder,Long>, JpaSpecificationExecutor<TestOrder> {
    List<TestOrder> findByPatientId(Long patientId);
    List<TestOrder> findByStatus(TestOrderStatus status);
    List<TestOrder> findByCreatedBy(Long createdBy);

//    // ✅ Lấy tất cả test order của 1 bệnh nhân, chưa bị xóa
//    List<TestOrder> findByPatientIdAndDeletedFalse(Long patientId);

//    @EntityGraph(attributePaths = {"testResults.parameter", "comments"})
//    Optional<TestOrder> findById(Long id);

    Optional<TestOrder> findFirstByBarcodeOrderByCreatedAtDesc(String barcode);
    Optional<TestOrder> findTopByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<TestOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
