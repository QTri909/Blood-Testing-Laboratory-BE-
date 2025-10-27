package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.entities.SyncedConfiguration;

import java.util.List;

public interface ISyncedConfigurationService {
    public void syncedConfiguration();
    public List<SyncedConfiguration> fetchFromExternalSystem();
    public SyncedConfiguration getActiveConfiguration(String key);
}
