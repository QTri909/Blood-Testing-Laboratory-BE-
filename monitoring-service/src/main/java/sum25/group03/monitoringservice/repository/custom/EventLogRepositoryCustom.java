package sum25.group03.monitoringservice.repository.custom;

import sum25.group03.monitoringservice.model.EventLog;

import java.util.List;

public interface EventLogRepositoryCustom {
    List<EventLog> searchEventLogs(String action, String message, String operator);
}
