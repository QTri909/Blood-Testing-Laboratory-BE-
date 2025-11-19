package sum25.group03.testorderservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.common.response.dtos.grpc.ParameterGrpc;
import sum25.group03.common.response.dtos.grpc.ParameterGrpcResponse;
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
        log.info("Id: " + parameter.getId()
        +"\nParamCode: "+ parameter.getParamCode()
        +"\nDescription: "+ parameter.getDescription()
        +"\nMin: "+ parameter.getMin()
        +"\nMax: "+ parameter.getMax()
        +"\nUnit: "+ parameter.getUnit()
        +"\nTimestamp: "+ LocalDateTime.now());
        parameter.setMin(dto.getMinValue());
        parameter.setMax(dto.getMaxValue());
        parameter.setDescription(dto.getDescription());
        parameter.setUnit(parameter.getUnit());
        parameterRepository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public ParameterResponseDTO getParameterById(Long id) {
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
        List<Parameter> parameters = parameterRepository.findByStatus(ParameterStatus.ACTIVE);
        return parameters.stream()
                .map(parameterMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ParameterResponseDTO getParameterByCode(String paramCode) {
        Parameter parameter = parameterRepository.findByParamCodeAndStatus(paramCode, ParameterStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter not found with code: " + paramCode));

        return parameterMapper.toResponseDto(parameter);
    }

    @Override
    @Transactional
    public ParameterGrpcResponse syncParameterFromWarehouse(List<ParameterGrpc> requestList) {

        List<Parameter> newParameters = parameterMapper.toParameterListFromParameterGrpcList(requestList);

        // save all new parameters to the database:
        parameterRepository.saveAll(newParameters);

        // map to response object:
        ParameterGrpcResponse response = ParameterGrpcResponse.builder()
                .success(true)
                .message("Synced " + newParameters.size() + " parameters successfully.")
                .build();

        return response;
    }
}