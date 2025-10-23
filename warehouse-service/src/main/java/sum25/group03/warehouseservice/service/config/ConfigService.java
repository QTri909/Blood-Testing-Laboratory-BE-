package sum25.group03.warehouseservice.service.config;

import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateGlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateSpecificConfigReq;

public interface ConfigService {
    ConfigurationDTO findByInstrumentId(Long id);
    boolean existsById(Long id);
    void createGlobalConfig(ConfigReq config);
    void updateGlobalConfig(UpdateGlobalConfigReq config);
    void updateSpecificConfig(UpdateSpecificConfigReq config);

    void deleteById(Long id);
}
