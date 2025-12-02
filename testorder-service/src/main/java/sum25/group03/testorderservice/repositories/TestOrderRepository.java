package sum25.group03.testorderservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.dtos.response.TestOrderSummaryByStatusChart;
import sum25.group03.testorderservice.dtos.response.TestOrderSummaryChart;
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
    Page<TestOrder> findByStatus(TestOrderStatus status, Pageable pageable);
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

    // first test order by barcode and status ONGOING
    Optional<TestOrder> findByBarcodeAndStatus(String barcode, TestOrderStatus status);

    Optional<TestOrder> findTopByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<TestOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Optional<TestOrder> findByBarcode(String barcode);


    @Query("""
    SELECT new sum25.group03.testorderservice.dtos.response.TestOrderSummaryChart(
        CAST(t.createdAt AS date),
        COUNT(t),
        t.type
    )
    FROM TestOrder t
    GROUP BY CAST(t.createdAt AS date), t.type
    ORDER BY CAST(t.createdAt AS date), t.type
""")
    List<TestOrderSummaryChart> getTestOrderSummaryByType();

    // find all test orders by its status:
    List<TestOrder> findAllByStatus(TestOrderStatus status);

    // summary of test orders by status between fromDate and toDate
    @Query("""
    SELECT new sum25.group03.testorderservice.dtos.response.TestOrderSummaryByStatusChart(
        CAST(t.createdAt AS date), 
        t.status, 
        COUNT(t)
    )
    FROM TestOrder t
    WHERE CAST(t.createdAt AS date) BETWEEN :fromDate AND :toDate
    GROUP BY CAST(t.createdAt AS date), t.status
    ORDER BY CAST(t.createdAt AS date), t.status
""")
    List<TestOrderSummaryByStatusChart> getTestOrderSummaryByStatusBetween(
            @Param("fromDate") java.time.LocalDate fromDate,
            @Param("toDate") java.time.LocalDate toDate
    );


}
