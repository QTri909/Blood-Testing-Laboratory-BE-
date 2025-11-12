package sum25.group03.monitoringservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.repository.EventLogRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class EventLogService {
    private final EventLogRepository eventLogRepo;

    public EventLogService(EventLogRepository eventLogRepository) {
        this.eventLogRepo = eventLogRepository;

    }
    // send mock

    public EventLog addEventLog(EventLog eventLog){
        eventLog.setCreatedAt(Instant.now());
        return eventLogRepo.save(eventLog);
    }
    public Page<EventLog> getAllEventLogs(int page, int size){
        return eventLogRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }
    public Optional<EventLog> getEventLog(String id) {
        return eventLogRepo.findById(id);
    }
    public List<EventLog> searchEventLogs(String action, String message, String operator) {
        return eventLogRepo.searchEventLogs(action, message, operator);
    }

}
