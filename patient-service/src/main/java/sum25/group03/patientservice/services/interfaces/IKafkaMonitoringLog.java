package sum25.group03.patientservice.services.interfaces;

import sum25.group03.common.response.events.MonitoringLogEvent;

public interface IKafkaMonitoringLog {
    void sendMonitoringLog(MonitoringLogEvent monitoringLogEvent);
}
