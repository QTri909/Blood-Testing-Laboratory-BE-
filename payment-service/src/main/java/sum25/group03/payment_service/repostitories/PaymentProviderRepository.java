package sum25.group03.payment_service.repostitories;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.payment_service.entities.PaymentProvider;

public interface PaymentProviderRepository extends JpaRepository<PaymentProvider,String> {
}
