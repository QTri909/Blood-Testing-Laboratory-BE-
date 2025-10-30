package sum25.group03.payment_service.repostitories;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.payment_service.entities.PaymentRequest;

import java.util.Optional;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, String> {
    Optional<PaymentRequest> findByTxnRef(String txnRef);
}
