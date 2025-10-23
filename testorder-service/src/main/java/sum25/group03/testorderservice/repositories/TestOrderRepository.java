package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entity.TestOrder;

@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder, Long> , JpaSpecificationExecutor<TestOrder> {
}
