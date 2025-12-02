package sum25.group03.testorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "sum25.group03.testorderservice",
        "sum25.group03.common"
})
@EnableScheduling
public class TestorderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestorderServiceApplication.class, args);
	}

}
