package sum25.group03.testorderservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import sum25.group03.testorderservice.entities.TestOrder;



@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder,Long>, JpaSpecificationExecutor<TestOrder> {
    Page<TestOrder> findByPatientId(Long patientId, Pageable pageable);
    List<TestOrder> findByPatientId(Long patientId);
    List<TestOrder> findByStatus(TestOrderStatus status);
    List<TestOrder> findByCreatedBy(Long createdBy);
    List<TestOrder> findAllByExternalMedicalRecordId(Long externalMedicalRecordId, Sort sort);

    @NonNull
    @EntityGraph(attributePaths = {"testResults", "testResults.parameter"})
    Optional<TestOrder> findById(@NonNull Long id);

    @NonNull
    @EntityGraph(attributePaths = {"testResults"})
    Optional<TestOrder> findByCode(@NonNull UUID code);

    // find all by list of ids:
    List<TestOrder> findByIdInOrderByCreatedAtDesc(List<Long> ids);

    Optional<TestOrder> findFirstByBarcodeOrderByCreatedAtDesc(String barcode);
    Optional<TestOrder> findTopByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<TestOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

}
