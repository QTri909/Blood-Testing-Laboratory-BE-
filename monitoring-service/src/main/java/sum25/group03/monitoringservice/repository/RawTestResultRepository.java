package sum25.group03.monitoringservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sum25.group03.monitoringservice.model.RawTestResult;

import java.util.List;
import java.util.Optional;

public interface RawTestResultRepository extends MongoRepository<RawTestResult,String> {
    Optional<RawTestResult> findFirstByTestOrderId(String testOrderId);
    List<RawTestResult> findAllByOrderByReceivedAtDesc();
    Optional<RawTestResult> findByBarcode(String barcode);
    List<RawTestResult> findByStatus(String status);
}
