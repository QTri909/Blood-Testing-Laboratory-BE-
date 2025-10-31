package sum25.group03.monitoringservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.event.TestResultPublishedEvent;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.service.EventLogService;
import sum25.group03.monitoringservice.service.RawTestResultService;
import sum25.group03.monitoringservice.util.RawTestVerifier;

import java.time.Instant;

/**
 * Capture all incoming test result messages automatically.
 */
@Slf4j
@Service
public class TestResultListener {

    private final RawTestResultService rawService;
    private final EventLogService eventLogService;
    private final RawTestVerifier verifier;
    @Autowired
    private ObjectMapper mapper;

    public TestResultListener(RawTestResultService rawService,
                              EventLogService eventLogService,
                              RawTestVerifier verifier) {
        this.rawService = rawService;
        this.eventLogService = eventLogService;
        this.verifier = verifier;
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @KafkaListener(topics = "raw-test-results", groupId = "monitoring-service")
    public void onMessage(String message) {
        try {
            log.info("Captured new raw test message: {}", message);

            RawTestResult raw = mapper.readValue(message, RawTestResult.class);
            raw.setReceivedAt(Instant.now());

            // Insert into MongoDB
            RawTestResult saved = rawService.addRawTestResult(raw);

            // Verify data correctness
            boolean verified = verifier.verify(raw, saved);

            if (verified) {
                // Log success
                eventLogService.addEventLog(
                        sum25.group03.monitoringservice.model.EventLog.builder()
                                .topic("raw-test-results")
                                .action("BACKUP_SUCCESS")
                                .message("Stored raw result " + raw.getTestOrderId())
                                .operator("system")
                                .createdAt(Instant.now())
                                .build()
                );
                log.info("Backup successful for testOrderId={}", raw.getTestOrderId());
            } else {
                log.warn("Verification failed for testOrderId={}", raw.getTestOrderId());
            }

        } catch (Exception e) {
            log.error("Failed to process raw test result message: {}", e.getMessage(), e);
            throw new RuntimeException(e); // triggers retry
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @KafkaListener(topics = "test-results-hl7", groupId = "monitoring-service-hl7")
    public void onTestResultPublished(String message) {
        try {
            log.info("Captured new HL7 test result event: {}", message);

            TestResultPublishedEvent event = mapper.readValue(message, TestResultPublishedEvent.class);

            // Save to consolidated RawTestResult collection
            RawTestResult saved = rawService.saveTestResultFromEvent(
                    String.valueOf(event.getTestOrderId()),
                    String.valueOf(event.getInstrumentId()),
                    event.getBarcode(),
                    event.getHl7Message(),
                    event.getRawData(),
                    event.getStatus()
            );

            if (saved != null) {
                // Log success
                eventLogService.addEventLog(
                        sum25.group03.monitoringservice.model.EventLog.builder()
                                .topic("test-results-hl7")
                                .action("TEST_RESULT_RECEIVED")
                                .message("Stored HL7 test result for barcode: " + event.getBarcode())
                                .operator("system")
                                .createdAt(Instant.now())
                                .build()
                );
                log.info("HL7 test result saved successfully for barcode={}", event.getBarcode());
            } else {
                log.warn("Failed to save HL7 test result for barcode={}", event.getBarcode());
            }

        } catch (Exception e) {
            log.error("Failed to process HL7 test result event: {}", e.getMessage(), e);
            throw new RuntimeException(e); // triggers retry
        }
    }
}
