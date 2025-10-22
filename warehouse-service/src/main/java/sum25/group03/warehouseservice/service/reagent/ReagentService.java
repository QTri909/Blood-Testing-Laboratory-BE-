package sum25.group03.warehouseservice.service.reagent;

import sum25.group03.warehouseservice.dto.response.ReagentValidationResponse;

import java.util.List;

public interface ReagentService {
    List<Long> findExistingIds(List<Long> reagentIds);

    ReagentValidationResponse validateReagent(String batchNumber, Double requiredVolume);
}
