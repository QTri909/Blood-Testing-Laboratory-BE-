package sum25.group03.monitoringservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.repository.RawTestResultRepository;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class RawTestResultService {
    private final RawTestResultRepository repository;
    public RawTestResultService(RawTestResultRepository repository) { this.repository = repository; }
    public Page<RawTestResult> getAll(int page, int size) { return repository.findAllByOrderByReceivedAtDesc(PageRequest.of(page, size)); }
    public Optional<RawTestResult> getById(String id) { return repository.findById(id); }
    public Page<RawTestResult> getFiltered(
            int page,
            int size,
            String testOrderId,
            String instrumentId,
            String status,
            String fromStr,
            String toStr
    ) {
        Instant from = null;
        Instant to = null;
        try {
            if (fromStr != null && !fromStr.isEmpty()) {
                from = Instant.parse(fromStr);
            }
            if (toStr != null && !toStr.isEmpty()) {
                to = Instant.parse(toStr);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid from/to date format. Use ISO-8601 string.");
        }

        Pageable pageable = PageRequest.of(page, size);
        return repository.searchRawTests(testOrderId, instrumentId, status, from, to, pageable);
    }


}