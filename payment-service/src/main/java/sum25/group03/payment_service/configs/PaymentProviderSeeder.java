package sum25.group03.payment_service.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sum25.group03.payment_service.entities.PaymentProvider;
import sum25.group03.payment_service.enums.PaymentProviderCode;
import sum25.group03.payment_service.enums.PaymentProviderStatus;
import sum25.group03.payment_service.repostitories.PaymentProviderRepository;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProviderSeeder implements CommandLineRunner {

    private final PaymentProviderRepository repository;

    @Override
    public void run(String... args) throws Exception {
        seedPaymentProviders();
    }

    private void seedPaymentProviders() {
        if (repository.count() > 0)
            return;

        // for vnpay
        PaymentProvider vnpayProvider = PaymentProvider.builder()
                .code(PaymentProviderCode.VN_PAY)
                .name("VNPay")
                .status(PaymentProviderStatus.ACTIVE)
                .build();

        // for paypal
        PaymentProvider paypalProvider = PaymentProvider.builder()
                .code(PaymentProviderCode.PAYPAL)
                .name("PayPal")
                .status(PaymentProviderStatus.INACTIVE)
                .build();

        // for stripe
        PaymentProvider stripeProvider = PaymentProvider.builder()
                .code(PaymentProviderCode.STRIPE)
                .name("Stripe")
                .status(PaymentProviderStatus.INACTIVE)
                .build();

        // save all:
        repository.saveAll(
                List.of(vnpayProvider, paypalProvider, stripeProvider)
        );

        log.info("seeded payment providers: vnpay, paypal, stripe");
    }

}
