package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.constants.KafkaConsumerVars;
import sum25.group03.testorderservice.dtos.request.KafkaParameterRequestDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.mapper.ParameterMapper;
import sum25.group03.testorderservice.repositories.ParameterRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParameterKafkaConsumerImpl {

    private final ParameterRepository parameterRepository;
    private final ParameterMapper parameterMapper;


    @KafkaListener(topics = KafkaConsumerVars.PARAMETER_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void listenParameterTopic(KafkaParameterRequestDTO dtoParameter) {

        Parameter searchedByCode = parameterRepository.findByParamCode(dtoParameter.getParamCode());
        Parameter newEntity = parameterMapper.fromKafkaDto(dtoParameter);

        if (searchedByCode == null) {
            parameterRepository.save(newEntity);
            log.info("Saved new parameter {}",newEntity);
            return;
        }

        // else: update existing entity
        parameterMapper.updateFromKafkaDto(dtoParameter, newEntity);
        log.info("Updated existing parameter {}",newEntity);
    }
}
