package sum25.group03.monitoringservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.repository.custom.EventLogRepositoryCustom;

import java.util.List;

public interface EventLogRepository extends MongoRepository<EventLog,String>, EventLogRepositoryCustom {
    List<EventLog> findAllByOrderByCreatedAtDesc();

}
