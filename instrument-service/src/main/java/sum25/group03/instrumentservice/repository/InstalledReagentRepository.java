package sum25.group03.instrumentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.model.InstalledReagent;

import java.util.List;

@Repository
public interface InstalledReagentRepository extends JpaRepository<InstalledReagent, Long> {
    List<InstalledReagent> findByInstrumentId(Long instrumentId);
    List<InstalledReagent> findByStatus(InstalledReagentStatus status);
    @Query("SELECT ir FROM InstalledReagent ir WHERE ir.instrument.id = :instrumentId")
    Page<InstalledReagent> findByInstrumentIdPaged(@Param("instrumentId") Integer instrumentId, Pageable pageable);

    @Query("SELECT ir FROM InstalledReagent ir WHERE ir.status = :status")
    Page<InstalledReagent> findByStatusPaged(@Param("status") InstalledReagentStatus status, Pageable pageable);

    @Query("SELECT ir FROM InstalledReagent ir WHERE ir.instrument.id = :instrumentId AND ir.status = :status")
    Page<InstalledReagent> findByInstrumentIdAndStatus(@Param("instrumentId") Integer instrumentId,
                                                       @Param("status") InstalledReagentStatus status,
                                                       Pageable pageable);

    @Query("SELECT ir FROM InstalledReagent ir WHERE " +
            "LOWER(ir.instrument.instrumentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(ir.lotReagentId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "CAST(ir.currentVolume AS string) LIKE CONCAT('%', :keyword, '%')")
    Page<InstalledReagent> searchByKeywords(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT ir FROM InstalledReagent ir WHERE " +
            "(LOWER(ir.instrument.instrumentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(ir.lotReagentId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "CAST(ir.currentVolume AS string) LIKE CONCAT('%', :keyword, '%')) AND " +
            "ir.status = :status")
    Page<InstalledReagent> searchByKeywordsAndStatus(@Param("keyword") String keyword,
                                                     @Param("status") InstalledReagentStatus status,
                                                     Pageable pageable);

    @Query("SELECT ir FROM InstalledReagent ir WHERE " +
            "(LOWER(ir.instrument.instrumentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(ir.lotReagentId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "CAST(ir.currentVolume AS string) LIKE CONCAT('%', :keyword, '%')) AND " +
            "ir.instrument.id = :instrumentId")
    Page<InstalledReagent> searchByKeywordsAndInstrumentId(@Param("keyword") String keyword,
                                                           @Param("instrumentId") Integer instrumentId,
                                                           Pageable pageable);

    @Query("SELECT ir FROM InstalledReagent ir WHERE " +
            "(LOWER(ir.instrument.instrumentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(ir.lotReagentId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "CAST(ir.currentVolume AS string) LIKE CONCAT('%', :keyword, '%')) AND " +
            "ir.status = :status AND ir.instrument.id = :instrumentId")
    Page<InstalledReagent> searchByKeywordsStatusAndInstrumentId(@Param("keyword") String keyword,
                                                                 @Param("status") InstalledReagentStatus status,
                                                                 @Param("instrumentId") Integer instrumentId,
                                                                 Pageable pageable);

    @Query("SELECT ir FROM InstalledReagent ir")
    Page<InstalledReagent> findAllReagents(Pageable pageable);
    List<InstalledReagent> findByInstrumentIdAndStatusIsNot(Long instrumentId, InstalledReagentStatus status);
}

