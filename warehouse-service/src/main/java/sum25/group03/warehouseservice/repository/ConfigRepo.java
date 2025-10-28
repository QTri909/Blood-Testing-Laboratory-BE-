package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.Configuration;


@Repository
public interface ConfigRepo extends JpaRepository<Configuration, Long> {

    @Query("""
        SELECT c
        FROM Configuration c
        WHERE c.active = true
    """)
    Page<Configuration> findAllByActiveTrue(Pageable pageable);

    @Query("""
    SELECT i.configuration
    FROM Instrument i
    WHERE i.instrumentId = :id AND i.configuration.active = true
""")
    Configuration findByInstrumentId(@Param("id") Long id);

    @Query("""
        SELECT c
            FROM Configuration c
        WHERE c.configurationId = :id AND c.active = true
    """)
    Configuration findByConfigId(@Param("id") Long id);
}
