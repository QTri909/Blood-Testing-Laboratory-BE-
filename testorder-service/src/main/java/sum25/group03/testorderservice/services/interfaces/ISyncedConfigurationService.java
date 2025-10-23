package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.entity.SyncedConfiguration;

import java.util.List;

public interface ISyncedConfigurationService {
    public void syncedConfiguration();
    public List<SyncedConfiguration> fetchFromExternalSystem();
    public SyncedConfiguration getActiveConfiguration(String key);
}
