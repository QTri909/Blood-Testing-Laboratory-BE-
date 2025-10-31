package sum25.group03.instrumentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.common.InstrumentStatus;
import sum25.group03.instrumentservice.model.Instrument;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    List<Instrument> findByStatus(InstrumentStatus status);

    @Query("SELECT DISTINCT i FROM Instrument i LEFT JOIN i.configuration c WHERE " +
            "lower(i.instrumentName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(c.configurationName) LIKE lower(CONCAT('%', :keyword, '%'))")
    Page<Instrument> searchByKeywords(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT i FROM Instrument i LEFT JOIN i.configuration c WHERE " +
            "i.status = :status AND (" +
            "lower(i.instrumentName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(c.configurationName) LIKE lower(CONCAT('%', :keyword, '%')))")
    Page<Instrument> searchByKeywordsAndStatus(@Param("keyword") String keyword,
                                               @Param("status") InstrumentStatus status,
                                               Pageable pageable);

    @Query("SELECT i FROM Instrument i WHERE i.status = :status")
    Page<Instrument> findAllByStatus(@Param("status") InstrumentStatus status, Pageable pageable);

    @Query("SELECT i FROM Instrument i")
    Page<Instrument> findAllInstruments(Pageable pageable);

    boolean existsByIdAndStatusIsNot(Long id, InstrumentStatus status);
}
