package sum25.group03.monitoringservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sum25.group03.monitoringservice.model.EventLog;

import java.util.List;

public interface EventLogRepository extends MongoRepository<EventLog,String> {
    List<EventLog> findAllByOrderByCreatedAtDesc();

}
