package sum25.group03.monitoringservice.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.monitoringservice.model.RawTestResult;

import java.time.Instant;

public interface RawTestResultRepositoryCustom {
    Page<RawTestResult> searchRawTests(
            String testOrderId,
            String instrumentId,
            String status,
            Instant from,
            Instant to,
            Pageable pageable
    );
}
