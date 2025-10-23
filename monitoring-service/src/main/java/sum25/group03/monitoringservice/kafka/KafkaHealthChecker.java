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

    private boolean lastStatusHealthy = true;

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
        int retryCount = 0;
        boolean healthy = false;

        while (retryCount < 3) {
            if (isKafkaHealthy()) {
                healthy = true;
                break;
            } else {
                retryCount++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        meterRegistry.gauge("kafka_health_status", healthy ? 1 : 0);

        if (!healthy) {
            if (lastStatusHealthy) {
                // Ghi log lỗi lần đầu Kafka down
                healthCheckLogRepository.save(
                        HealthCheckLog.builder()
                                .timestamp(Instant.now())
                                .status("ERROR")
                                .errorCode("BROKER_UNAVAILABLE")
                                .retryCount(retryCount)
                                .recoveryEvent(false)
                                .build()
                );
            }
            lastStatusHealthy = false;
        } else {
            if (!lastStatusHealthy) {
                // Ghi log phục hồi
                healthCheckLogRepository.save(
                        HealthCheckLog.builder()
                                .timestamp(Instant.now())
                                .status("RECOVERED")
                                .retryCount(0)
                                .recoveryEvent(true)
                                .build()
                );
            }
            lastStatusHealthy = true;
        }
    }

    private boolean isKafkaHealthy() {
        try (AdminClient adminClient = createAdminClient()) {
            DescribeClusterResult cluster = adminClient.describeCluster();
            cluster.nodes().get(); // nếu lỗi, sẽ throw exception
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
