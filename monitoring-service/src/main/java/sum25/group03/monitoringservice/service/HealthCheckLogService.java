package sum25.group03.monitoringservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.model.HealthCheckLog;
import sum25.group03.monitoringservice.repository.HealthCheckLogRepository;

import java.util.Optional;

@Service
public class HealthCheckLogService {
    private final HealthCheckLogRepository repository;

    public HealthCheckLogService(HealthCheckLogRepository repository) {
        this.repository = repository;
    }

    public Page<HealthCheckLog> getAllLogs(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Optional<HealthCheckLog> getLogById(String id) {
        return repository.findById(id);
    }

    public Optional<HealthCheckLog> getLatestLog() {
        return repository.findFirstByOrderByTimestampDesc();
    }
}
