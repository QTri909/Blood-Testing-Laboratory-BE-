package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.dtos.request.ParameterRequestDTO;
import sum25.group03.testorderservice.dtos.request.SyncedConfigurationDTO;
import sum25.group03.testorderservice.dtos.response.ParameterResponseDTO;

import java.util.List;

public interface ParameterService {
    void updateParameter(SyncedConfigurationDTO dto);
    ParameterResponseDTO getParameterById(Long id);
    List<ParameterResponseDTO> getAllParameters();
    ParameterResponseDTO getParameterByCode(String paramCode);
}