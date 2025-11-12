package sum25.group03.testorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "sum25.group03.testorder-service",
        "sum25.group03.common"
})
public class TestorderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestorderServiceApplication.class, args);
	}

}
