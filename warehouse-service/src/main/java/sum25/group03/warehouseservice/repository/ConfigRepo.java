package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.entity.Configurations;

@Repository
public interface ConfigRepo extends JpaRepository<Configurations, Long> {

    @Query("""
        SELECT new sum25.group03.warehouseservice.dto.internal.ConfigurationDTO(
            c.configurationKey,
            c.configurationValue,
            c.configurationCategory,
            c.instrumentType,
            c.description,
            c.unit,
            c.active
        )
        FROM Configurations c
        JOIN Instrument i ON c.configurationId = i.configuration.configurationId
        WHERE i.instrumentId = :id AND c.deleted = false
    """)
    ConfigurationDTO findByInstrumentId(@Param("id") Long id);
}
