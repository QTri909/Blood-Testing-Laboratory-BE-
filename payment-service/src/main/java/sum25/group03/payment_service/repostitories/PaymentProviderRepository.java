package sum25.group03.payment_service.repostitories;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.payment_service.entities.PaymentProvider;
import sum25.group03.payment_service.enums.PaymentProviderCode;
import sum25.group03.payment_service.enums.PaymentProviderStatus;

import java.util.Optional;

public interface PaymentProviderRepository extends JpaRepository<PaymentProvider,String> {
    Optional<PaymentProvider> findByCodeAndStatus(PaymentProviderCode code, PaymentProviderStatus status);
}