package sum25.group03.warehouseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstrumentRepo extends JpaRepository<Instrument,Long> {
    boolean existsBySerialNumber(String serialNumber);

    @Query("""
            SELECT i FROM Instrument i
            WHERE (:name IS NULL OR :name = '' OR LOWER(i.instrumentName) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:model IS NULL OR :model = '' OR LOWER(i.model) LIKE LOWER(CONCAT('%', :model, '%')))
            AND (:status IS NULL OR i.status = :status)
    """)
    Page<Instrument> searchInstruments(
            @Param("name") String name,
            @Param("model") String model,
            @Param("status") InstrumentStatus status,
            Pageable pageable
    );

    @Query("SELECT i FROM Instrument i WHERE i.status = 'DELETED' AND i.updatedAt < :threshold")
    List<Instrument> findInactiveBefore(LocalDate threshold);

    List<Instrument> findByStatusAndAutoDeleteScheduledAtBefore(InstrumentStatus status, LocalDate date);

    @EntityGraph(attributePaths = {"configuration", "reagentHistoryUsages", "reagentHistoryUsages.reagent"})
    @Query("SELECT i FROM Instrument i WHERE i.instrumentId = :id")
    Optional<Instrument> findInstrumentById(@Param("id") Long id);


    @Query("""
    SELECT i FROM Instrument i
    WHERE (:key IS NULL OR :key = '' OR LOWER(i.instrumentName) LIKE LOWER(CONCAT('%', :key, '%')))
    ORDER BY i.createdAt DESC, i.instrumentId DESC
""")
    Page<Instrument> searchInstrumentsByName(@Param("key") String key, Pageable pageable);


    @Query("""
        SELECT i FROM Instrument i
        WHERE i.status = 'ACTIVE'
    """)
    List<Instrument> findAllByStatusActive();
}
