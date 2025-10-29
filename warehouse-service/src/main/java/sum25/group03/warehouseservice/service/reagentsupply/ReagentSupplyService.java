package sum25.group03.warehouseservice.service.reagentsupply;

import sum25.group03.warehouseservice.dto.request.ReagentSupplyReq;
import sum25.group03.warehouseservice.dto.response.HistorySupplyRes;
import sum25.group03.warehouseservice.dto.response.PageRes;

public interface ReagentSupplyService {
    PageRes<HistorySupplyRes> getAll(int page, int size);
    void addReagentSupply(ReagentSupplyReq reagentSupplyReq);
}
