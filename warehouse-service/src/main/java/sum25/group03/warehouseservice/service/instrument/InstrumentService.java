package sum25.group03.warehouseservice.service.instrument;

import sum25.group03.warehouseservice.dto.request.AssignConfigAndReagentReq;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.dto.response.InstrumentConfigReagentRes;
import sum25.group03.warehouseservice.dto.response.InstrumentResponse;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.dto.response.PageRes;

import java.util.List;

public interface InstrumentService {
    InstrumentResponse addInstrumentToWarehouse(InstrumentReq instrument);
    InstrumentConfigReagentRes addConfigAndReagentToInstrument(AssignConfigAndReagentReq req);
    InstrumentStatusResponse getInstrumentStatus(Long instrumentId);
    InstrumentConfigReagentRes getInstrumentById(Long instrumentId);
    PageRes<InstrumentResponse> getAllInstruments(int page, int size, String key);
    List<InstrumentResponse> getList();
}
