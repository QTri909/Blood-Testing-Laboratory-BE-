package sum25.group03.payment_service.repostitories;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.payment_service.entities.PaymentProvider;
import sum25.group03.payment_service.enums.PaymentProviderCode;

public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, String> {
    PaymentProvider findByCode(PaymentProviderCode stripe);
}
