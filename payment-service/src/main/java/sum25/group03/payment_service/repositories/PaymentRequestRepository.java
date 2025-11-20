package sum25.group03.payment_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import sum25.group03.payment_service.entities.PaymentRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest,String> {
    Optional<PaymentRequest> findByTxnRef(String txnRef);
    List<PaymentRequest> findAllByUserId(Long userId);
    Optional<PaymentRequest> findByOrderCode(String orderCode);
    List<PaymentRequest> findAllByOrderCode(String orderCode);

    @NonNull
    @EntityGraph(attributePaths = {"paymentProvider"})
    Page<PaymentRequest> findAll(@NonNull Pageable pageable);
}
