package sum25.group03.testorderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.request.ParameterRequestDTO;
import sum25.group03.testorderservice.dtos.response.ParameterResponseDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.enums.ParameterStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.mapper.ParameterMapper;
import sum25.group03.testorderservice.repositories.ParameterRepository;
import sum25.group03.testorderservice.service.interfaces.ParameterService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParameterServiceImpl implements ParameterService {


    private final ParameterRepository parameterRepository;
    private final ParameterMapper parameterMapper;

    @Override
    public ParameterResponseDTO createParameter(ParameterRequestDTO requestDTO) {
        log.info("Creating new parameter with code: {}", requestDTO.getParamCode());

        if (parameterRepository.existsByParamCode(requestDTO.getParamCode())) {
            throw new IllegalArgumentException("Parameter with code " + requestDTO.getParamCode() + " already exists");
        }

        Parameter parameter = parameterMapper.toEntity(requestDTO);
        parameter.setStatus(ParameterStatus.ACTIVE);
        parameter.setCreatedAt(LocalDate.now());
        parameter.setUpdatedAt(LocalDate.now());

        Parameter savedParameter = parameterRepository.save(parameter);
        log.info("Parameter created successfully with id: {}", savedParameter.getId());

        return parameterMapper.toResponseDto(savedParameter);
    }

    @Override
    public ParameterResponseDTO updateParameter(Long id, ParameterRequestDTO requestDTO) {
        log.info("Updating parameter with id: {}", id);

        Parameter existingParameter = parameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter not found with id: " + id));

        if (existingParameter.getStatus() == ParameterStatus.INACTIVE) {
            throw new IllegalStateException("Cannot update inactive parameter");
        }

        parameterMapper.updateEntity(requestDTO, existingParameter);
        existingParameter.setUpdatedAt(LocalDate.now());

        Parameter updatedParameter = parameterRepository.save(existingParameter);
        log.info("Parameter updated successfully with id: {}", updatedParameter.getId());

        return parameterMapper.toResponseDto(updatedParameter);
    }

    @Override
    public void deleteParameter(Long id) {
        log.info("Deleting parameter with id: {}", id);

        Parameter parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter not found with id: " + id));

        if (parameter.getStatus() == ParameterStatus.INACTIVE) {
            throw new IllegalStateException("Parameter already deleted");
        }

        parameter.setStatus(ParameterStatus.INACTIVE);
        parameter.setUpdatedAt(LocalDate.now());

        parameterRepository.save(parameter);
        log.info("Parameter deleted successfully with id: {}", id);
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