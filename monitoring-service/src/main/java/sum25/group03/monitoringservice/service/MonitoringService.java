package sum25.group03.monitoringservice.service;

import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.kafka.MockProducer;
import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.model.RawTestResult;

import java.util.List;
import java.util.Optional;

@Service
public class MonitoringService {
    private final EventLogService eventLogService;
    private final RawTestResultService rawTestResultService;
    // mock producer
    private final MockProducer mockProducer;
    public MonitoringService(EventLogService eventLogService, RawTestResultService rawTestResultService, MockProducer mockProducer) {
        this.eventLogService = eventLogService;
        this.rawTestResultService = rawTestResultService;
        this.mockProducer = mockProducer;
    }
    // send mock
    public void sendMessage(){
        mockProducer.sendMockEvent();
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
    public List<EventLog> searchEventLogs(String action, String message, String operator) {
        return eventLogService.searchEventLogs(action, message, operator);
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
