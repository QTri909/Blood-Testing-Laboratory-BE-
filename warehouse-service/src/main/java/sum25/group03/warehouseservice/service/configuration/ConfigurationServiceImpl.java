package sum25.group03.warehouseservice.service.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.internal.ConfigurationDTO;
import sum25.group03.warehouseservice.repository.ConfigRepo;

@Service
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements  ConfigurationService {
    private final ConfigRepo configRepo;
    @Override
    public ConfigurationDTO findByInstrumentId(Long id) {
        return configRepo.findByInstrumentId(id);
    }

    @Override
    public boolean existsById(Long id) {
        return configRepo.existsById(id);
    }
}
