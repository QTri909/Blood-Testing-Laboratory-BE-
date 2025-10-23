package sum25.group03.instrumentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InstrumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstrumentServiceApplication.class, args);
    }

}
