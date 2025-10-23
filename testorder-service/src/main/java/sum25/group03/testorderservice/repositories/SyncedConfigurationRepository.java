package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entity.SyncedConfiguration;
import sum25.group03.testorderservice.enums.SyncedConfigurationStatus;

@Repository
public interface SyncedConfigurationRepository extends JpaRepository<SyncedConfiguration,Long> {
    SyncedConfiguration findByConfigKeyAndStatus(String configKey, SyncedConfigurationStatus status);
}
