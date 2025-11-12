package sum25.group03.monitoringservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sum25.group03.monitoringservice.event.RawTestResultEvent;
import sum25.group03.monitoringservice.service.RawTestResultBackupService;

@Component
@Slf4j
public class RawTestResultConsumer {

    @Autowired
    private RawTestResultBackupService backupService;

    @KafkaListener(topics = "instrument.testresults", groupId = "monitoring-service")
    public void listen(RawTestResultEvent event) {
        log.info("[Kafka] Received RawTestResultEvent for orderId={}", event.getOrderId());
        backupService.backupNewRawTestResult(event);
    }
}
