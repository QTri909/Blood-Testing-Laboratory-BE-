package sum25.group03.payment_service.repostitories;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.payment_service.entities.PaymentTransaction;

import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
}
