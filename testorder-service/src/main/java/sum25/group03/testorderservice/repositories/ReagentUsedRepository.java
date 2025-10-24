package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entity.ReagentUsed;

import java.util.List;

@Repository
public interface ReagentUsedRepository extends JpaRepository<ReagentUsed,Long> {
    List<ReagentUsed> findByReagentId(Long reagentId);
}
