package sum25.group03.warehouseservice.service.config;

import org.springframework.data.domain.Page;
import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.dto.request.GlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.SpecificConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateGlobalConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateSpecificConfigReq;
import sum25.group03.warehouseservice.entity.Configuration;

import java.util.Optional;

public interface ConfigService {
    ConfigurationDTO findByInstrumentId(Long id);
    boolean existsById(Long id);
    void createGlobalConfig(GlobalConfigReq config);
    void createSpecificConfig(SpecificConfigReq config);
    void updateGlobalConfig(UpdateGlobalConfigReq config);
    void updateSpecificConfig(UpdateSpecificConfigReq config);

    void deleteSpecificById(Long id);
    void deleteGlobalById(Long id);
    Page<GlobalConfiguration> getAllGlobalConfig(int page, int size);
    Page<SpecificConfiguration> getAllSpecificConfig(int page, int size);
    SpecificConfiguration getSpecificConfigByInstrumentId(Long id);
    GlobalConfiguration getGlobalConfigById(Long id);
    Page<Configuration> getAllSpecificConfig(int page, int size);
}
