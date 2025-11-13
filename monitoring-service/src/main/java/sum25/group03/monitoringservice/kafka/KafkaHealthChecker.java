package sum25.group03.monitoringservice.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sum25.group03.monitoringservice.model.HealthCheckLog;
import sum25.group03.monitoringservice.repository.HealthCheckLogRepository;

import java.time.Instant;
import java.util.Properties;

@Component
public class KafkaHealthChecker {

    private final HealthCheckLogRepository healthCheckLogRepository;
    private final MeterRegistry meterRegistry;
    private final String bootstrapServers;

    private boolean lastHealthy = true;

    public KafkaHealthChecker(
            HealthCheckLogRepository healthCheckLogRepository,
            MeterRegistry meterRegistry,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
    ) {
        this.healthCheckLogRepository = healthCheckLogRepository;
        this.meterRegistry = meterRegistry;
        this.bootstrapServers = bootstrapServers;
    }

    @Scheduled(fixedDelay = 60000)
    public void checkKafkaHealth() {
        boolean healthy = false;
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            if (isKafkaHealthy()) {
                healthy = true;
                break;
            }
            try {
                Thread.sleep(2000); // chờ 2 giây giữa các lần thử
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


        meterRegistry.gauge("kafka_health_status", healthy ? 1 : 0);

        // Chỉ ghi log khi trạng thái thay đổi
        if (healthy != lastHealthy) {
            healthCheckLogRepository.save(
                    HealthCheckLog.builder()
                            .timestamp(Instant.now())
                            .status(healthy ? "HEALTHY" : "UNHEALTHY")
                            .build()
            );
            lastHealthy = healthy;
        }
    }

    private boolean isKafkaHealthy() {
        try (AdminClient adminClient = createAdminClient()) {
            DescribeClusterResult cluster = adminClient.describeCluster();
            cluster.nodes().get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private AdminClient createAdminClient() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("request.timeout.ms", "3000");
        return AdminClient.create(props);
    }
}
