package sum25.group03.monitoringservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class MonitoringServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
