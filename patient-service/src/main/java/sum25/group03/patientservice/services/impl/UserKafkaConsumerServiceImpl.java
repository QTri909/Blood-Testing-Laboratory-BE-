package sum25.group03.patientservice.services.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.dtos.request.KafkaUserDTO;
import sum25.group03.patientservice.entities.UserSnapshotEntity;
import sum25.group03.patientservice.mapper.UserSnapshotMapper;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.services.interfaces.UserKafkaConsumerService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserKafkaConsumerServiceImpl implements UserKafkaConsumerService {

    private final UserSnapshotRepository userRepository;
    private final UserSnapshotMapper userMapper;

    @Override
    @Transactional
    public void fetchUserFromKafka(@Valid KafkaUserDTO kafkaUserDTO) {
        Long externalUserId = kafkaUserDTO.getId();

        // search for entity in the database:
        UserSnapshotEntity searchedEntity = userRepository.findByExternalUserId(externalUserId).
                orElse(null);

        if (searchedEntity != null) {
            // exists, update it
            log.info("Updating entity {} ...", searchedEntity);
            userMapper.updateEntityFromKafkaDTO(kafkaUserDTO, searchedEntity);
            log.info("Entity is now: {}", searchedEntity);
            return;
        }

        // else: not exist, insert one
        UserSnapshotEntity newEntity = userMapper.fromUserKafkaDTO(kafkaUserDTO);
        userRepository.save(newEntity);
        log.info("Inserted new entity: {}", newEntity);
    }
}
