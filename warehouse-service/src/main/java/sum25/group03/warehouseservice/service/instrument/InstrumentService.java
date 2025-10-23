package sum25.group03.warehouseservice.service.instrument;

import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.entity.Instrument;

public interface InstrumentService {
    void addInstrumentToWarehouse(InstrumentReq instrument);
    Instrument findById(Long id);
}
