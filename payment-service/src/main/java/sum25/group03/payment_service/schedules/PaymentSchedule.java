package sum25.group03.payment_service.schedules;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sum25.group03.payment_service.services.interfaces.PaymentRequestService;

@Component
@RequiredArgsConstructor
public class PaymentSchedule {

    private final PaymentRequestService paymentRequestService;

    private static final String TRIGGER_EACH_15_MINUTES = "0 0/15 * * * ?";

    @Scheduled(cron = TRIGGER_EACH_15_MINUTES)
    public void triggerCancelPendingPaymentRequests() {
        paymentRequestService.cancelAllPendingPaymentRequests();
    }
}
