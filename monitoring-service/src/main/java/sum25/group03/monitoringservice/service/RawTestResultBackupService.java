package sum25.group03.monitoringservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.event.RawTestResultEvent;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.repository.RawTestResultRepository;
import sum25.group03.monitoringservice.util.RawTestVerifier;

import java.time.Instant;

@Service
@Slf4j
public class RawTestResultBackupService {

    private final RawTestResultRepository repository;
    private final RawTestVerifier verifier;

    @Autowired
    public RawTestResultBackupService(RawTestResultRepository repository, RawTestVerifier verifier) {
        this.repository = repository;
        this.verifier = verifier;
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void backupNewRawTestResult(RawTestResultEvent event) {
        try {
            log.info("[RawTestResultBackup] Capturing event for orderId={}", event.getOrderId());

            if (!verifier.isValid(event)) {
                log.error("[RawTestResultBackup] Invalid event data: {}", event);
                return;
            }

            RawTestResult entity = new RawTestResult();
            entity.setTestOrderId(event.getOrderId());
            entity.setInstrumentId(event.getInstrumentId());
            entity.setRawData(event.getResultData().toString());
            entity.setReceivedAt(event.getTimestamp());
            entity.setStatus("RECEIVED");

            RawTestResult saved = repository.save(entity);
            log.info("[RawTestResultBackup] Backup success for orderId={}", event.getOrderId());

            verifier.verifyBackupMatch(event, saved);

        } catch (Exception e) {
            log.error("[RawTestResultBackup] Error while saving backup: {}", e.getMessage(), e);
            throw e;
        }
    }

    public RawTestResult saveTestResultFromEvent(String testOrderId, String instrumentId, String barcode,
                                                 String hl7Message, String rawData, String status) {
        RawTestResult result = RawTestResult.builder()
                .testOrderId(testOrderId)
                .instrumentId(instrumentId)
                .barcode(barcode)
                .hl7Payload(hl7Message)
                .rawData(rawData)
                .status(status)
                .receivedAt(Instant.now())
                .build();

        try {
            RawTestResult saved = repository.save(result);
//            backupLogs.add("SUCCESS (Kafka): " + testOrderId + " - " + barcode + " at " + result.getReceivedAt());
            return saved;
        } catch (Exception e) {
//            failedInsertions.add(result);
            log.error("LỖI KHI LƯU VÀO MONGODB: ", e);
            return null;
        }
    }

}
