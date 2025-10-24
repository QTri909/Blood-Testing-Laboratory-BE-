package sum25.group03.testorderservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.dtos.request.SyncedConfigurationDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.repositories.ParameterRepository;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ParameterServiceImpl {

    @Autowired
    private ParameterRepository parameterRepository;

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
}
