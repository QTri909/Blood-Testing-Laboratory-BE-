package sum25.group03.monitoringservice.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.monitoringservice.model.EventLog;

public interface EventLogRepositoryCustom {
    Page<EventLog> searchEventLogs(
            String action,
            String message,
            String operator,
            String sourceService,
            Pageable pageable
    );
}
