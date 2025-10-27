package sum25.group03.warehouseservice.service.instrument;

import sum25.group03.warehouseservice.dto.request.AssignConfigAndReagentReq;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.dto.response.InstrumentConfigReagentRes;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;

public interface InstrumentService {
    void addInstrumentToWarehouse(InstrumentReq instrument);
    InstrumentConfigReagentRes addConfigAndReagentToInstrument(AssignConfigAndReagentReq assignConfigAndReagentReq);
    InstrumentStatusResponse getInstrumentStatus(Long instrumentId);

}
