package sum25.group03.monitoringservice.service;

import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.model.RawTestResult;

import java.util.List;
import java.util.Optional;

@Service
public class MonitoringService {
    private final EventLogService eventLogService;
    private final RawTestResultService rawTestResultService;
    public MonitoringService(EventLogService eventLogService, RawTestResultService rawTestResultService) {
        this.eventLogService = eventLogService;
        this.rawTestResultService = rawTestResultService;
    }
    // Event logs
    public EventLog addEventLog(EventLog eventLog) {
        return eventLogService.addEventLog(eventLog);
    }
    public Optional<EventLog> getEventLog(String id) {
        return eventLogService.getEventLog(id);
    }
    public List<EventLog> getAllEventLogs() {
        return eventLogService.getAllEventLogs();
    }
    // Raw results
    public RawTestResult addRawTestResult(RawTestResult rawTestResult) {
        return rawTestResultService.addRawTestResult(rawTestResult);
    }
    public Optional<RawTestResult> findRawByTestOrderId(String id) {
        return rawTestResultService.findRawByTestOrderId(id);
    }
    public List<RawTestResult> getAllRawResults(){
        return rawTestResultService.getAllRawResults();
    }
}
