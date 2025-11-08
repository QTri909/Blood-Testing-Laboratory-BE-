package sum25.group03.testorderservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.request.ParameterRequestDTO;
import sum25.group03.testorderservice.dtos.request.SyncedConfigurationDTO;
import sum25.group03.testorderservice.dtos.response.ParameterResponseDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.enums.ParameterStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.mapper.ParameterMapper;
import sum25.group03.testorderservice.repositories.ParameterRepository;
import sum25.group03.testorderservice.services.interfaces.ParameterService;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParameterServiceImpl implements ParameterService {


    private final ParameterRepository parameterRepository;
    private final ParameterMapper parameterMapper;

    public void updateParameter(SyncedConfigurationDTO dto){
        Parameter parameter = parameterRepository.findByParamCode(dto.getConfigKey());
        if(parameter == null){
            throw new EntityNotFoundException("Parameter not found");
        }
        if(dto.getMinValue() >= dto.getMaxValue()){
            throw new InvalidParameterException("maxValue must be greater than minValue");
        }
        log.info("Id: "+ parameter.getId());
        log.info("ParamCode: "+ parameter.getParamCode());
        log.info("Description: "+ parameter.getDescription());
        log.info("Min: "+ parameter.getMin());
        log.info("Max: "+ parameter.getMax());
        log.info("Unit: "+ parameter.getUnit());
        log.info("Timestamp: " + LocalDateTime.now());
        parameter.setMin(dto.getMinValue());
        parameter.setMax(dto.getMaxValue());
        parameter.setDescription(dto.getDescription());
        parameter.setUnit(parameter.getUnit());
        parameterRepository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public ParameterResponseDTO getParameterById(Long id) {
        log.info("Fetching parameter with id: {}", id);

        Parameter parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter not found with id: " + id));

        if (parameter.getStatus() == ParameterStatus.INACTIVE) {
            throw new ResourceNotFoundException("Parameter has been deleted");
        }

        return parameterMapper.toResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParameterResponseDTO> getAllParameters() {
        log.info("Fetching all active parameters");

        List<Parameter> parameters = parameterRepository.findByStatus(ParameterStatus.ACTIVE);
        return parameters.stream()
                .map(parameterMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ParameterResponseDTO getParameterByCode(String paramCode) {
        log.info("Fetching parameter with code: {}", paramCode);

        Parameter parameter = parameterRepository.findByParamCodeAndStatus(paramCode, ParameterStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter not found with code: " + paramCode));

        return parameterMapper.toResponseDto(parameter);
    }
}