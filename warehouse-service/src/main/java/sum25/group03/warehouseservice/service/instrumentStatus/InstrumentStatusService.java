package sum25.group03.warehouseservice.service.instrumentStatus;

import sum25.group03.warehouseservice.entity.enums.OperationalStatus;

public interface InstrumentStatusService {
    void activateInstrument(Long id, String username);
    void deactivateInstrument(Long id, String username);
    void deleteInstrument(Long id, String username);
    OperationalStatus checkInstrumentStatus(Long id);
}
