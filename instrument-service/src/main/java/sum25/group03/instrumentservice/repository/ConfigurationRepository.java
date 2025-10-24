package sum25.group03.instrumentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.model.Configuration;

import java.util.Optional;
import java.util.List;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {
    Optional<Configuration> findByConfigKey(String configKey);
    List<Configuration> findByConfigCategory(String configCategory);
    List<Configuration> findByInstrumentType(String instrumentType);
    List<Configuration> findByIsActiveTrueAndIsDeletedFalse();
}

