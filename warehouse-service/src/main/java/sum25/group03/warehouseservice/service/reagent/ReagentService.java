package sum25.group03.warehouseservice.service.reagent;

import sum25.group03.warehouseservice.dto.response.*;
import sum25.group03.warehouseservice.entity.Reagents;

import java.util.List;

public interface ReagentService {
    List<Long> findExistingIds(List<Long> reagentIds);
    List<Reagents> findAllByInstrumentId(Long instrumentId);
    List<Reagents> findAllByReagentId(List<Long> reagentId);
    void deleteReagent(Long reagentId);
    ReagentValidationResponse validateReagent(String batchNumber, Double requiredVolume);

    List<ReagentResponseForInstrument> listReagentsForInstrument();

    List<ReagentRes> getAllReagents();
    PageRes<ReagentListItemRes> getReagentListItems(int page, int size);
    ReagentDetailRes getReagentDetail(Long reagentId);
}
