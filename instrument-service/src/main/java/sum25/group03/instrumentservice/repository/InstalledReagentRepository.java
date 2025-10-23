package sum25.group03.instrumentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.model.InstalledReagent;

import java.util.List;

@Repository
public interface InstalledReagentRepository extends JpaRepository<InstalledReagent, Long> {
    List<InstalledReagent> findByInstrumentId(Long instrumentId);
    List<InstalledReagent> findByStatus(InstalledReagentStatus status);
}

