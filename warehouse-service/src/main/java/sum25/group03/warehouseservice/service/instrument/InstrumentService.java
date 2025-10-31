package sum25.group03.warehouseservice.service.instrument;

import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;

public interface InstrumentService {
//    void addInstrumentToWarehouse(InstrumentReq instrument);

    InstrumentStatusResponse getInstrumentStatus(Long instrumentId);
}
