package sum25.group03.warehouseservice.service.config;

import org.springframework.data.domain.Page;
import sum25.group03.warehouseservice.dto.request.ConfigReq;
import sum25.group03.warehouseservice.dto.request.UpdateConfigReq;
import sum25.group03.warehouseservice.dto.response.ConfigRes;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.entity.Configuration;

public interface ConfigService {
    boolean existsById(Long id);
    void createConfig(ConfigReq config);
    void updateConfig(UpdateConfigReq config);

    void deleteById(Long id);;
    PageRes<ConfigRes> getAllConfig(int page, int size);
    Configuration getConfigByInstrumentId(Long id);
    Configuration getConfigById(Long id);
    PageRes<ConfigRes> searchConfigs(String keyword, String id, int page, int size);
}
