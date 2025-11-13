package sum25.group03.monitoringservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.repository.custom.EventLogRepositoryCustom;

public interface EventLogRepository extends MongoRepository<EventLog,String>, EventLogRepositoryCustom {
    Page<EventLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
