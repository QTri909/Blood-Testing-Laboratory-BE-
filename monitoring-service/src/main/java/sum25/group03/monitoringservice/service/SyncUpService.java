package sum25.group03.monitoringservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.event.SyncUpEvent;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.repository.RawTestResultRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SyncUpService {

    @Autowired
    private RawTestResultRepository repository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String INSTRUMENT_REQUEST_TOPIC = "instrument.raw.request";
    private static final String TEST_ORDER_REPUBLISH_TOPIC = "testorder.sync.republish";

    /**
     * Xử lý event sync-up từ Test Order Service
     */
    public void processSyncUp(SyncUpEvent event) {
        log.info("[SyncUp] Received sync-up event for TestOrderId={}", event.getOrderId());

        List<RawTestResult> existingResults = repository.findByTestOrderId(event.getOrderId());
        if (existingResults.isEmpty()) {
            log.warn("[SyncUp] Missing results for order {}, requesting from Instrument Service", event.getOrderId());
            kafkaTemplate.send(INSTRUMENT_REQUEST_TOPIC, event.getOrderId());
            return;
        }

        event.setRawResults(existingResults);
        kafkaTemplate.send(TEST_ORDER_REPUBLISH_TOPIC, event);
        log.info("[SyncUp] Republished updated sync-up for order {}", event.getOrderId());
    }

    /**
     * Re-sync thủ công theo yêu cầu admin
     */
    public boolean manualResync(String orderId) {
        Optional<RawTestResult> result = repository.findFirstByTestOrderId(orderId);
        if (result.isPresent()) {
            SyncUpEvent event = new SyncUpEvent(orderId, List.of(result.get()));
            kafkaTemplate.send(TEST_ORDER_REPUBLISH_TOPIC, event);
            log.info("[SyncUp] Manual re-sync triggered for {}", orderId);
            return true;
        }
        log.warn("[SyncUp] No result found for manual re-sync: {}", orderId);
        return false;
    }
}
