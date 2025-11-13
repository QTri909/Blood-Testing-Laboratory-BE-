package sum25.group03.monitoringservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sum25.group03.monitoringservice.event.RawTestResultEvent;
import sum25.group03.monitoringservice.model.RawTestResult;

@Slf4j
@Component
public class RawTestVerifier {

    public boolean isValid(RawTestResultEvent event) {
        return event != null 
                && event.getOrderId() != null 
                && event.getInstrumentId() != null;
    }

    public boolean verifyBackupMatch(RawTestResultEvent event, RawTestResult stored) {
        if (event == null || stored == null) return false;

        boolean match = event.getOrderId().equals(stored.getTestOrderId())
                && event.getInstrumentId().equals(stored.getInstrumentId());

        log.info("[Verifier] Backup verification {} for orderId={}",
                match ? "PASSED" : "FAILED", event.getOrderId());

        return match;
    }
}
