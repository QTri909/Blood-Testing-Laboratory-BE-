package sum25.group03.monitoringservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sum25.group03.monitoringservice.event.SyncUpEvent;
import sum25.group03.monitoringservice.service.SyncUpService;

@Component
@Slf4j
public class SyncUpConsumer {

    @Autowired
    private SyncUpService syncUpService;

    @KafkaListener(topics = "testorder.syncup", groupId = "monitoring-service")
    public void listen(SyncUpEvent event) {
        log.info("[Kafka] Received SyncUpEvent for orderId={}", event.getOrderId());
        syncUpService.processSyncUp(event);
    }
}
