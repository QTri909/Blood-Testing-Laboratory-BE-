package sum25.group03.monitoringservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import sum25.group03.monitoringservice.model.HealthCheckLog;

import java.util.Optional;

public interface HealthCheckLogRepository extends MongoRepository<HealthCheckLog, String> {
    Page<HealthCheckLog> findAll(Pageable pageable);
    Optional<HealthCheckLog> findFirstByOrderByTimestampDesc();
}
