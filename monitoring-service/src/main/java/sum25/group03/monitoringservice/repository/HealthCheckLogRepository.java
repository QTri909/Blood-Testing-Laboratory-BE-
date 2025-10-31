package sum25.group03.monitoringservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sum25.group03.monitoringservice.model.HealthCheckLog;

public interface HealthCheckLogRepository extends MongoRepository<HealthCheckLog, String> {

}
