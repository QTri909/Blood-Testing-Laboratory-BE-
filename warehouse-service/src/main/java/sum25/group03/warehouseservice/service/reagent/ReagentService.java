package sum25.group03.warehouseservice.service.reagent;

import sum25.group03.warehouseservice.dto.request.ReagentReq;
import sum25.group03.warehouseservice.dto.response.*;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;

import java.util.List;

public interface ReagentService {
    List<Long> findExistingIds(List<Long> reagentIds);
    List<Reagents> findAllByInstrumentId(Long instrumentId);
    List<Reagents> findAllByReagentIdAndStatus(List<Long> reagentId, ReagentStatus status);
    void deleteReagent(Long reagentId);
    ReagentValidationResponse validateReagent(String batchNumber, Double requiredVolume);

    List<ReagentResponseForInstrument> listReagentsForInstrument();


    List<ReagentRes> getAllReagents();
    PageRes<ReagentListItemRes> getReagentListItems(int page, int size);
    List<ReagentListItemRes> getReagentListItems();

    ReagentDetailRes getReagentDetail(Long reagentId);
    //add reagents
    ReagentRes createReagent(ReagentReq req);
    ReagentRes getReagentById(Long reagentId);
    List<ReagentInventoryRes> getListLotNumberByReagentId(Long reagentId);

    void updateInventoryStatusesScheduler();

}
