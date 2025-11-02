package sum25.group03.payment_service.repostitories;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.payment_service.entities.PaymentRequest;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, String> { // it should be UUID, not String
}
