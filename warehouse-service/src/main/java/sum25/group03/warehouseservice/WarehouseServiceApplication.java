package sum25.group03.warehouseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "sum25.group03.warehouseservice",
        "sum25.group03.common"
})
@EnableScheduling
public class WarehouseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseServiceApplication.class, args);
    }

}
