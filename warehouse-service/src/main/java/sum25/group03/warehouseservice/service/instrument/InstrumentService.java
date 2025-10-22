package sum25.group03.warehouseservice.service.instrument;

import sum25.group03.warehouseservice.dto.request.InstrumentReq;

public interface InstrumentService {
    void addInstrumentToWarehouse(InstrumentReq instrument);
    void activateInstrument(Long id, String username);
    void deactivateInstrument(Long id, String username);
}
