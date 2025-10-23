package sum25.group03.instrumentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.instrumentservice.common.InstrumentStatus;
import sum25.group03.instrumentservice.model.Instrument;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    List<Instrument> findByStatus(InstrumentStatus status);
}
