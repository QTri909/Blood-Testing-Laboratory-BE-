package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.common.response.events.MonitoringLogEvent;

public interface IKafkaMonitoringLog {
    void publishMonitoringLog(MonitoringLogEvent kafkaMonitoringLog);
}
