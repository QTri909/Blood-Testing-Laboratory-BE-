package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.dto.request.ParameterRequestDTO;
import sum25.group03.testorderservice.dto.response.ParameterResponseDTO;

import java.util.List;

public interface ParameterService {

    ParameterResponseDTO createParameter(ParameterRequestDTO requestDTO);

    ParameterResponseDTO updateParameter(Long id, ParameterRequestDTO requestDTO);

    void deleteParameter(Long id);

    ParameterResponseDTO getParameterById(Long id);

    List<ParameterResponseDTO> getAllParameters();

    ParameterResponseDTO getParameterByCode(String paramCode);
}