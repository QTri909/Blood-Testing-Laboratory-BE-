package sum25.group03.warehouseservice.service.reagent;

import java.util.List;

public interface ReagentService {
    List<Long> findExistingIds(List<Long> reagentIds);
}
