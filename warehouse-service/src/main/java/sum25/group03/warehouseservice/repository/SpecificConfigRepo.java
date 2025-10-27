package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.entity.Configuration;

@Repository
public interface SpecificConfigRepo extends JpaRepository<Configuration, Long> {
    @Query("""
        SELECT new sum25.group03.warehouseservice.dto.internal.ConfigurationDTO(
            g.globalConfigurationId,
            c.supportedTests,
            c.parameterSettings,
            c.dataOutputFormat,
            c.communicationProtocol,
            c.mixingSpeed
        )
        FROM Configuration c
        JOIN Instrument i ON c.specificConfigurationId = i.configuration.specificConfigurationId
        JOIN GlobalConfiguration g ON c.globalConfiguration.globalConfigurationId = g.globalConfigurationId
        WHERE i.instrumentId = :id AND c.active = true AND g.active = true
    """)
    ConfigurationDTO findByInstrumentId(@Param("id") Long id);

    @Query("""
        SELECT c
        FROM Configuration c
        WHERE c.active = true
    """)
    Page<Configuration> findAllByActiveTrue(Pageable pageable);
}
