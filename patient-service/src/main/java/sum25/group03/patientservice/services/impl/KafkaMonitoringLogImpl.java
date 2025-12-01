package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.constants.KafkaConstants;
import sum25.group03.common.response.events.MonitoringLogEvent;
import sum25.group03.patientservice.services.interfaces.IKafkaMonitoringLog;

@Service
@RequiredArgsConstructor
public class KafkaMonitoringLogImpl implements IKafkaMonitoringLog {

    private final KafkaTemplate<String, MonitoringLogEvent> kafkaTemplate;

    @Override
    public void sendMonitoringLog(MonitoringLogEvent monitoringLogEvent) {
        kafkaTemplate.send(KafkaConstants.MONITORING_LOG_TOPIC, monitoringLogEvent);
    }
}
