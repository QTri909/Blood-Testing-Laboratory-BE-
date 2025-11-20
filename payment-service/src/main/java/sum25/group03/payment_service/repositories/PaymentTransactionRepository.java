package sum25.group03.payment_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import sum25.group03.payment_service.entities.PaymentTransaction;

import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    @NonNull
    @EntityGraph(attributePaths = {"paymentRequest"})
    Page<PaymentTransaction> findAll(@NonNull Pageable pageable);
}
