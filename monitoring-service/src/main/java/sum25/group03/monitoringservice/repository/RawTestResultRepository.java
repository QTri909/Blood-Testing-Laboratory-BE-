package sum25.group03.monitoringservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.repository.custom.RawTestResultRepositoryCustom;

import java.util.List;
import java.util.Optional;

public interface RawTestResultRepository extends MongoRepository<RawTestResult, String>, RawTestResultRepositoryCustom {
    List<RawTestResult> findByTestOrderId(String testOrderId);
    Optional<RawTestResult> findFirstByTestOrderId(String testOrderId);


    List<RawTestResult> findAllByOrderByReceivedAtDesc();
    Page<RawTestResult> findAllByOrderByReceivedAtDesc(Pageable pageable);

    Optional<RawTestResult> findByBarcode(String barcode);
    List<RawTestResult> findByStatus(String status);
}
