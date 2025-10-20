package sum25.group03.warehouseservice.service.installedreagent;

import java.util.List;

public interface InstalledReagentService {
    List<Long> findIdsByInstrumentId(Long instrumentId);
}
