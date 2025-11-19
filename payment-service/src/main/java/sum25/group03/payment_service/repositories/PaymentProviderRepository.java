package sum25.group03.payment_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.payment_service.entities.PaymentProvider;
import sum25.group03.payment_service.enums.PaymentProviderCode;
import sum25.group03.payment_service.enums.PaymentProviderStatus;

import java.util.Optional;

@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider,String> {
    PaymentProvider findByCode(PaymentProviderCode stripe);
    Optional<PaymentProvider> findByCodeAndStatus(PaymentProviderCode code, PaymentProviderStatus status);

    PaymentProvider findByName(String stripe);
}
