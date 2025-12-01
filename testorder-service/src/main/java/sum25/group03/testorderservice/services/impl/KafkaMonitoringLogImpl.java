package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.constants.KafkaConstants;
import sum25.group03.common.response.events.MonitoringLogEvent;
import sum25.group03.testorderservice.services.interfaces.IKafkaMonitoringLog;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaMonitoringLogImpl implements IKafkaMonitoringLog {

    private final KafkaTemplate<String, MonitoringLogEvent> kafkaMonitoringLogTemplate;

    @Override
    public void publishMonitoringLog(MonitoringLogEvent monitoringLogEvent) {
        kafkaMonitoringLogTemplate.send(
                KafkaConstants.MONITORING_LOG_TOPIC,
                monitoringLogEvent
        );
    }
}
