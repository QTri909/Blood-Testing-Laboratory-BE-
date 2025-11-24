package sum25.group03.instrumentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.events.TestResultPublishedEvent;
import sum25.group03.instrumentservice.model.RawTestResult;
import sum25.group03.instrumentservice.repository.RawTestResultRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultEventListener {
    private final RawTestResultRepository rawTestResultRepo;

    @KafkaListener(
            topics = "complete-sync-test-result-events",
            groupId = "instrument-service-group",
            containerFactory = "completeSyncTestResultListenerFactory"
    )
    public void handleUpdateStatusTestResult(@Payload TestResultPublishedEvent event) {
        try {
            log.info("Received TestResultPublishedEvent: {}", event);
            RawTestResult rawTestResult = rawTestResultRepo.findByBarcodeAndInstrumentIdAndTestOrderId(event.getBarcode(), event.getInstrumentId(), event.getTestOrderId());
            if (rawTestResult == null) {
                log.warn("No RawTestResult found for barcode={}, instrumentId={}, testOrderId={}",
                        event.getBarcode(), event.getInstrumentId(), event.getTestOrderId());
                return;
            }
            rawTestResult.setIsSynced(true);
            rawTestResultRepo.save(rawTestResult);
            log.info("TestResultPublishedEvent succesfully send TestResultPublishedEvent");

        } catch (Exception e) {
            log.error("Failed to process Reagent Usage Event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process Reagent Usage Event", e);
        }
    }
}
