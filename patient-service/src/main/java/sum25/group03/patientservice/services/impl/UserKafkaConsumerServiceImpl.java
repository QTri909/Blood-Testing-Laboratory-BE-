package sum25.group03.patientservice.services.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.common.response.events.UserCreatedEvent;
import sum25.group03.patientservice.constants.KafkaConstantVars;
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

    private final void handleFetchUpdatedAndCreatedUserFromIAM(UserCreatedEvent kafkaUserDTO) {
        Long externalUserId = Long.parseLong(kafkaUserDTO.getId());

        // 1. search for entity in the database:
        UserSnapshotEntity searchedEntity = userRepository.findByExternalUserId(externalUserId).
                orElse(null);

        if (searchedEntity != null) {
            // 2.1. exists, update it
            log.info("Updating entity {} ...", searchedEntity);
            userMapper.updateEntityFromKafkaDTO(kafkaUserDTO, searchedEntity);
            log.info("Entity is now: {}", searchedEntity);
            return;
        }

        // 2.2. else: not exist, insert one
        UserSnapshotEntity newEntity = userMapper.fromUserKafkaDTO(kafkaUserDTO);
        userRepository.save(newEntity);
        log.info("Inserted new entity: {}", newEntity);
    }

    @Override
    @Transactional
    @KafkaListener(
            topics = {
                    KafkaConstantVars.USER_TOPIC,
                    KafkaConstantVars.USER_CREATED_TOPIC,
                    KafkaConstantVars.USER_UPDATED_TOPIC
            },
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void fetchUserFromKafka(@Valid UserCreatedEvent kafkaUserDTO) {
        handleFetchUpdatedAndCreatedUserFromIAM(kafkaUserDTO);
    }
}
