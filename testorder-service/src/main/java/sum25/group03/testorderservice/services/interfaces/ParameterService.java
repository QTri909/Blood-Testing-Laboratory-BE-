package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.dtos.request.ParameterRequestDTO;
import sum25.group03.testorderservice.dtos.request.SyncedConfigurationDTO;
import sum25.group03.testorderservice.dtos.response.ParameterResponseDTO;

import java.util.List;

public interface ParameterService {
    void updateParameter(SyncedConfigurationDTO dto);

    ParameterResponseDTO createParameter(ParameterRequestDTO requestDTO);
    ParameterResponseDTO updateParameter(Long id, ParameterRequestDTO requestDTO);
    void deleteParameter(Long id);
    ParameterResponseDTO getParameterById(Long id);
    List<ParameterResponseDTO> getAllParameters();
    ParameterResponseDTO getParameterByCode(String paramCode);
}