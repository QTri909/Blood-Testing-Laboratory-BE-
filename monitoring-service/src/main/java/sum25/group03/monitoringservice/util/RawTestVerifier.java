package sum25.group03.monitoringservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sum25.group03.monitoringservice.model.RawTestResult;

/**
 * Verify that stored data matches published messages.
 */
@Slf4j
@Component
public class RawTestVerifier {

    public boolean verify(RawTestResult input, RawTestResult stored) {
        if (input == null || stored == null) return false;

        boolean match = input.getTestOrderId().equals(stored.getTestOrderId())
                && input.getInstrumentId().equals(stored.getInstrumentId())
                && input.getHl7Payload().equals(stored.getHl7Payload());

        log.info("Verification {} for testOrderId={}", match ? "PASSED" : "FAILED", input.getTestOrderId());
        return match;
    }
}
