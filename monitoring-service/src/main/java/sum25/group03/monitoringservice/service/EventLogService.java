package sum25.group03.monitoringservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.dto.EventLogDTO;
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

    public EventLog addEventLog(EventLog eventLog) {

        return eventLogRepo.save(eventLog);
    }

    public Page<EventLogDTO> getAllEventLogs(int page, int size) {
        Page<EventLog> eventLogs = eventLogRepo.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
        List<EventLogDTO> dtoList = eventLogs.getContent().stream()
                .map(this::convertToDTO)
                .toList();
        return new PageImpl<>(dtoList, eventLogs.getPageable(), eventLogs.getTotalElements());
    }

    public Optional<EventLog> getEventLog(String id) {
        return eventLogRepo.findById(id);
    }

    public List<EventLog> searchEventLogs(String action, String message, String operator, String sourceService) {
        return eventLogRepo.searchEventLogs(action, message, operator, sourceService);
    }

    private EventLogDTO convertToDTO(EventLog eventLog) {
        return EventLogDTO.builder()
                .id(eventLog.getId())
                .action(eventLog.getAction())
                .message(eventLog.getMessage())
                .sourceService(eventLog.getSourceService())
                .timestamp(eventLog.getTimestamp())
                .build();
    }
}
