package sum25.group03.warehouseservice.service.config;

import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;

public interface ConfigService {
    ConfigurationDTO findByInstrumentId(Long id);
    boolean existsById(Long id);
}
