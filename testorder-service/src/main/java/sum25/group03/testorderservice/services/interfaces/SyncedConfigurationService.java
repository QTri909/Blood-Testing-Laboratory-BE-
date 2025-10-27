package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.dtos.request.SyncedConfigurationDTO;

public interface SyncedConfigurationService {
    void handleConfigUpdate(SyncedConfigurationDTO dto);
}
