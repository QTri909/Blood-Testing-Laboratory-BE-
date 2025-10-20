package sum25.group03.warehouseservice.service.configuration;

import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;

public interface ConfigurationService {
    ConfigurationDTO findByInstrumentId(Long id);
    boolean existsById(Long id);
}
