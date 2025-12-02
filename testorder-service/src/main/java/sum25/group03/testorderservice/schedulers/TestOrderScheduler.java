package sum25.group03.testorderservice.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;

@Component
@RequiredArgsConstructor
public class TestOrderScheduler {

    private final TestOrderService testOrderService;

    private static final String EVERY_2_HOURS = "0 0 0/2 * * ?";

    @Scheduled(cron = EVERY_2_HOURS)
    public void cleanTestOrderRecords() {
        // + task 1: all empty record => removed
        testOrderService.removeEmptyTestOrders();

        // + task 2: all waiting_payment record => cancelled
        testOrderService.cancelAllWaitingPaymentTestOrders();
    }
}
