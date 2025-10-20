package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.dto.internal.ConfigIdAndReagentDTO;
import sum25.group03.warehouseservice.entity.Instrument;

import java.util.List;

@Repository
public interface InstrumentRepo extends JpaRepository<Instrument,Long> {
    boolean existsBySerialNumber(String serialNumber);

    @Query("""
        SELECT new sum25.group03.warehouseservice.dto.internal.ConfigIdAndReagentDTO(
            config.configurationId,
            reagent.reagent.reagentId
        )
        FROM Instrument ir
        LEFT JOIN ir.configuration config
        LEFT JOIN ir.reagentHistoryUsages reagent 
        WHERE ir.instrumentId = :instrumentId
    """)
    List<ConfigIdAndReagentDTO> findConfigAndReagentByInstrument(@Param("instrumentId") Long instrumentId);
}
